
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@page
	import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>
<%@page import="java.text.DecimalFormat"%>
<jsp:include page="checkVulnerability.jsp" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
	#RecordAnomalyTable a {
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
										<ul class="nav nav-tabs responsive" role="tablist"
											style="border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">											

											<li ><a onClick="validateHrefVulnerability(this)" 
												href="dashboard_table?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Count
														Reasonability</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="validity?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Microsegment Validity</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)"  href="dupstats?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Uniqueness</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="nullstats?idApp=${idApp}" style="padding: 2px 2px;">
													<strong>Completeness</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="badData?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>Conformity</strong>
											</a></li>

											<li ><a onClick="validateHrefVulnerability(this)" 
												href="sqlRules?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>Custom Rules</strong>
											</a></li>											
											
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="stringstats?idApp=${idApp}" style="padding: 2px 2px;">
													<strong>Drift & Orphan</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="numericstats?idApp=${idApp}" style="padding: 2px 2px;">
													<strong>Distribution Check</strong>
											</a></li>
											<li class="active" ><a onClick="validateHrefVulnerability(this)" 
												href="recordAnomaly?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Record
														Anomaly</strong></a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="timelinessCheck?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>Sequence</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
				                         		href="exceptions?idApp=${idApp}" 
				                         		style="padding: 2px 2px;"><strong>Exceptions</strong>
					                         </a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="rootCauseAnalysis?idApp=${idApp}"
												style="padding: 2px 2px;"><strong>RootCause Analysis</strong>
											</a></li>
											
											
										</ul>
									</div>
								</div>
							</div>
	<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<span class="caption-subject bold ">Record Anomaly <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${RecordAnomaly_TableName}','RecordAnomalyData')"
										 href="#"
										class="CSVStatus" id="">(Download CSV)</a>
			</span>
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
	<input type="hidden" id="tableName" value="${RecordAnomaly_TableName}">
	
	<div class="portlet-body">
		<table id="RecordAnomalyTable"
			class="table table-striped table-bordered  table-hover"
			style="width: 100%;">
			<thead>
				<tr>
                    <th>Date</th>
                    <th>Run</th>
                    <th>Col_Name</th>
                    <th>Col_Val</th>
                    <th>Mean</th>
                    <th>Std_Dev</th>
                    <th>Microsegment_Val</th>
                    <th>Microsegment</th>
                    <th>Status</th>
                    <th>Deviation</th>
                    <th>Threshold</th>
                    <th>Learn</th>
                    <th>Download File</th>
                </tr>
			</thead>

		</table>
	</div>
	<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<span class="caption-subject bold ">Record Anomaly (Historical Batches) <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${HistoryAnomaly_TableName}','HistoryAnomalyData')"
										 href="#"
										class="CSVStatus" id="">(Download CSV)</a>
			</span>
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
	<input type="hidden" id="HistorytableName"
		value="${HistoryAnomaly_TableName}">
	<div class="portlet-body">
		<table id="HistoryAnomalyTable"
			class="table table-striped table-bordered  table-hover"
			style="width: 100%;">
			<thead>
				<tr>
					<%
												try {
													if (request.getAttribute("data_Quality__history_anomalyTrue") != null) {
														SqlRowSet rs2 = (SqlRowSet) request.getAttribute("data_Quality__history_anomaly");
														SqlRowSetMetaData metadata2 = rs2.getMetaData();
														//for (int i = 1; i <= metadata2.getColumnCount(); i++) {
															//out.println("<th>" + metadata2.getColumnName(i) + "</th>");
															%>
															
															<th>Date</th>
															<th>Run</th>
															<th>Col_Name</th>
															<th>Col_Val</th>
															<th>Mean</th>
															<th>Std_Dev</th>
															<th>Microsegment_Val</th>
															<th>Microsegment</th>
															<!-- <th>Ra_Deviation</th> -->
															<th>Status</th>
															<th>DQI</th>
															
															<% 
															
														//}
													} else {
											%>

					<td>Date</td>
					<td>Run</td>
					<td>Col_Name</td>
					<td>Col_Val</td>
					<td>Mean</td>
					<td>Std_Dev</td>
					<td>Deviation</td>
					<td>status</td>
					<%
												}
												} catch (Exception e) {
											%><p>No data available in table</p>
					<%
												}
											%>



				</tr>
			</thead>
			<%--			<tbody>
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
																		if (rs2.getString(i) != null) {
																			if (rs2.getString(i).equalsIgnoreCase("passed")) {
																				out.println("<td><span class='label label-success label-sm'>"
																						+ rs2.getString(i) + "</span></td>");
																			} else if (rs2.getString(i).equalsIgnoreCase("failed")) {
																				out.println("<td><span class='label label-danger label-sm'>"
																						+ rs2.getString(i) + "</span></td>");
																			} else {
																				out.println("<td>" + rs2.getString(i) + "</td>");
																			}
																		} else {
																			out.println("<td>" + rs2.getString(i) + "</td>");
																		}
																		
																	}
																}
																out.println("</tr>");
															}
														}
											}catch(Exception e){
												%><p>No data available in table</p>
												<%
											}
												%>
									</tbody>
								</table>
							</div> --%>

		</table>
	</div>
<br/><br/>

<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<span class="caption-subject bold ">Date Rule Column Summary <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${Date_Rule_TableName}','DateRuleSummaryData')"
				 href="#"
				class="CSVStatus" id="">(Download CSV)</a></span>
		</div>
	</div>
	<hr />
	<input type="hidden" id="dateRuletableName"
		value="${Date_Rule_TableName}">
	<input type="hidden" id="idApp" value="${idApp}">
	<input type="hidden" id="idData" value="${idData}">
	<div class="portlet-body">
		<table id="DateRuleTable"
			class="table table-striped table-bordered  table-hover"
			style="width: 100%;">
			<thead>
				<tr>
					<th>Date</th>
					<th>Run</th>
					<th>Date_Field</th>
					<th>Total_Number_Of_Records</th>
					<th>Total_Failed_Records</th>

				</tr>
			</thead>

		</table>

	</div>

	<br/>
	<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<span class="caption-subject bold ">Date Rule Bad Data 
				 <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','DATA_QUALITY_DateRule_Summary','DateRule_BadData')"  href="#"
					class="CSVStatus" id="">(Download CSV)</a>
			</span>
		</div>
	</div>
	<br/><br/>
	<div class="portlet-title">
		<div class="caption font-red-sunglo">
			<span class="caption-subject bold ">Failed Date Rule Column
				Summary <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${Failed_Date_Rule_TableName}','DateRuleFailedData')"
				 href="#"
				class="CSVStatus" id="">(Download CSV)</a>
				
			</span>
		</div>
	</div>
	<hr />
	<input type="hidden" id="failedDateRuletableName"
		value="${Failed_Date_Rule_TableName}">
	<input type="hidden" id="idApp" value="${idApp}">
	<input type="hidden" id="idData" value="${idData}">
	<div class="portlet-body">
		<table id="failedDateRuleTable"
			class="table table-striped table-bordered  table-hover"
			style="width: 100%;">
			<thead>
				<tr>
				    <th>Date</th>
                    <th>Run</th>
                    <th>Date_Field_Cols</th>
                    <th>Date_Field_Values</th>
                    <th>d Group Val</th>
                    <th>d Group Col</th>
                    <th>Failure Reason</th>
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
	<jsp:include page="downloadCsvReports.jsp"/>
	<jsp:include page="footer.jsp" />
</body>



<script>

jQuery(document).ready(function() {
	
	var idApp = $("#idApp").val();
	var idData = $("#idData").val();
	var tableName1 = $("#dateRuletableName").val();
	/* alert("tableName1==>>>>>"+tableName1); */
	
table = $('#DateRuleTable').dataTable({
			"bPaginate" : true,
			"order" : [ 0, 'asc' ],
			"bInfo" : true,
			"iDisplayStart" : 0,
			"bProcessing" : true,
			"bServerSide" : true,
			"dataSrc" : "",
			'sScrollX' : true,
			"sAjaxSource" : path
					+ "/ColSummTableForDateRuleTableTab?tableName="
					+ tableName1+"&idApp="+idApp,
					"aoColumns": [
                        {"data" : "Date"},
                        {"data" : "Run"},
                        {"data" : "DateField"},
                        {"data" : "TotalNumberOfRecords"},
                        {"data" : "TotalFailedRecords"}
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

$(document).ready(function() {
	var table;
	console.log("hello");
	var tableName8=$( "#HistorytableName").val();
	//alert(tableName1);
	var tableName=$( "#tableName").val();
	//alert(tableName);
	var idApp=$( "#idApp" ).val();
	//alert(tableName + " " + idApp);
	//alert(tableName);
	
	if(tableName==$( "#tableName").val()){
		table = $('#RecordAnomalyTable').dataTable({
			  	"bPaginate": true,
			  	"order": [ 0, 'asc' ],
			  	"bInfo": true,
			  	"iDisplayStart":0,
			  	"bProcessing" : true,
			 	"bServerSide" : true,
			 	"dataSrc": "",
			 	'sScrollX' : true,
			 	"sAjaxSource" : path+"/recordAnomalyTable?tableName="+tableName+"&idApp="+idApp,
			 	"aoColumns": [
                            {"data" : "Date"},
                            {"data" : "Run"},
                            {"data" : "ColName"},
                            {"data" : "ColVal"},
                            {"data" : "mean"},
                            {"data" : "stddev"},
                            {"data" : "dGroupVal"},
                            {"data" : "dGroupCol"},
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
                            {"data" : "ra_Deviation"},
                            {"data" : "threshold"},
                            {"data" : "ra_Deviation",
                                 "render": function(data, type, row, meta){
                                      var failPercentage = row.ra_Deviation;
                                      var anamolyColName = row.ColName;
                                      data = "<a onClick=\"javascript:updateColumnCheckThreshold("+idApp+",'Record Anomaly Check','"+anamolyColName+"','"+failPercentage+"')\" class=\"fa fa-thumbs-down\" </a>";
                                  return data;
                                  }
                               },
                           
                            {"data" : "ColName",
                               "render": function(data, type, row, meta){
                                  data = "<a onclick=\"javascript:downloadCsvReports('"+idApp+"','"+tableName+"','RecordAnomalyData/"+data+"')\" href=\"#\" style=\"font-size: small;\">Download</a>";
                                   return data;
                               }
                           }
                 ],
                 'columnDefs': [ {
                     'targets': [11,12], // column index (start from 0)
                     'orderable': false, // set orderable false for selected columns
                  }],
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
		}
	
		if(tableName8==$("#HistorytableName").val()){
	//alert(tableName1);

	table = $('#HistoryAnomalyTable').dataTable({
		  	"bPaginate": true,
		  	"order": [ 0, 'asc' ],
		  	"bInfo": true,
		  	"iDisplayStart":0,
		  	"bProcessing" : true,
		 	"bServerSide" : true,
		 	"dataSrc": "",
		 	'sScrollX' : true,
		 	"sAjaxSource" : path+"/TranDetailIdentityTable?tableName="+tableName8+"&idApp="+idApp,
		 	"aoColumns": [
                    {"data" : "Date"},
                    {"data" : "Run"},
                    {"data" : "ColName"},
                    {"data" : "ColVal"},
                    {"data" : "mean"},
                    {"data" : "stddev"},
                    {"data" : "dGroupVal"},
                    {"data" : "dGroupCol"},
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
                    {"data" : "RA_Dqi"}

             ],
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
		}
	
	
});

jQuery(document).ready(function() {

    var idApp = $("#idApp").val();
	var idData = $("#idData").val();
	var tableName2 = $("#failedDateRuletableName").val();
	
	 table = $('#failedDateRuleTable').dataTable({
				"bPaginate" : true,
				"order" : [ 0, 'asc' ],
				"bInfo" : true,
				"iDisplayStart" : 0,
				"bProcessing" : true,
				"bServerSide" : true,
				"dataSrc" : "",
				'sScrollX' : true,
				"sAjaxSource" : path
						+ "/ColSummTableForFailedDateRuleTableTab?tableName="
						+ tableName2+"&idApp="+idApp,
				"aoColumns": [
                        {"data" : "Date"},
                        {"data" : "Run"},
                        {"data" : "DateFieldCols"},
                        {"data" : "DateFieldValues"},
                        {"data" : "dGroupVal"},
                        {"data" : "dGroupCol"},
                        {"data" : "FailureReason"}

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

	/* var table;
	console.log("hello");
	var tableName=$( "#tableName").val();
	
	var idApp=$( "#idApp" ).val();
	//alert(tableName + " " + idApp);
	//alert(tableName);
	if(tableName==$( "#tableName").val()){
	table = $('#RecordAnomalyTable').dataTable({
		  	"bPaginate": true,
		  	"order": [ 0, 'asc' ],
		  	"bInfo": true,
		  	"iDisplayStart":0,
		  	"bProcessing" : true,
		 	"bServerSide" : true,
		 	"dataSrc": "",
		 	'sScrollX' : true,
		 	"sAjaxSource" : path+"/recordAnomalyTable?tableName="+tableName+"&idApp="+idApp,
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
	} */
});



	$(document).ready(function() {
		$('.hrefCall').click(function() {
       	 $('#popSample').addClass('is-visible');
           var validationCheck=$(this).attr('id');
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
               success: function(listOfValues) {
                   console.log(listOfValues);
                   if (typeof listOfValues !== "undefined" && listOfValues != "") {
                   	
                   	google.load("visualization", "1", {packages:["corechart"]});
                       google.setOnLoadCallback(drawChart);
                       var data1 = new google.visualization.DataTable();
                       data1.addColumn('string', 'Date(Run)');
             		    data1.addColumn('number', 'DQI');
             		 
             		  //data1.addRow([new Date("2016-02-04"),90]);
                       $.each(listOfValues, function(key, value) {	
                       	$.each( value, function( i, l ){
                       		 // alert( "Index #" + i + ": " + l );
       					data1.addRow([key,l]);
                       	});
       				});
                       data1.sort({column: 0, asc: true});
                       var title="";
                       if(validationCheck=="Aggregate DQI Summary"){
                       	title=validationCheck;
                       }else{
                       	title=validationCheck + " DQI";
                       }
                       var len = $.map(listOfValues, function(n, i) { return i; }).length;
                       console.log(len);
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
                           title: title,
                           hAxis: {title: 'Date (Run)',  titleTextStyle: {color: '#333'},direction:-1, slantedText:true, slantedTextAngle:45,showTextEvery:showTextEvery},
                           vAxis: {minValue: 0},
                           chartArea: {bottom: 100},
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
               error: function(xhr, textStatus,
                   errorThrown) {
                   $('#initial').hide();
                   $('#fail').show();
               }
           });
       });
       $( "#dqiDataCloseBtn" ).click(function() {
       	  $('#popSample').removeClass('is-visible');
       	});
 
   });
</script> 

<script>
$( "#rcdiv" ).on( "click", function() {
    console.log("rcdiv");
    var form_data = {
             tableName : $('#tableName').val(),
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
                 $('#rcdivbar').removeClass('hidden');
                 $('.progressbar').css("width", percentage+'%');
                 $('.progressbar').html("Downloading CSV...   "+percentage+'%');
                  console.log(j_obj1);//return false;
                  if(percentage>95){
                      //toastr.info("CSV Downloaded successfully");
                          clearInterval(myInterval);
                          $('#rcdivbar').addClass('hidden');
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


/* jQuery(document).ready(function() {
	var table;
	console.log("hello");
	var tableName=$( "#tableName").val();
	var idApp=$( "#idApp" ).val();
	//alert(tableName);
	table = $('#RecordAnomalyTable').dataTable({
		  	"bPaginate": true,
		  	"order": [ 0, 'asc' ],
		  	"bInfo": true,
		  	"iDisplayStart":0,
		  	"bProcessing" : true,
		 	"bServerSide" : true,
		 	"dataSrc": "",
		 	'sScrollX' : true,
		 	"sAjaxSource" : path+"/recordAnomalyTable?tableName="+tableName+"&idApp="+idApp,
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
}); */

 
</script>
<script>
   
    /* $(document).ready( function () {
    	  var table = $('#dashboardTable').DataTable({
    	    "columnDefs": [
    	        {"className": "dt-center", "targets": "_all"}
    	      ],
    	      "bSort" : false,
    	      "searching": false,
    	      "lengthChange": false,
    	      "bLengthChange": false,
    	      "bPaginate": false,
    	      "bPaginate": false,
    	      "bLengthChange": false,
    	      "bFilter": true,
    	      "bInfo": false,
    	      "bAutoWidth": false
    	  });
    	} ); */
    </script>
<!-- this code for recordanomaly download csv bar -->
<script>

   	$( "#rcdiv" ).on( "click", function() {
   		console.log("rcdiv");
   		var form_data = {
   				 tableName : $('#tableName').val(),
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
   	                 $('#rcdivbar').removeClass('hidden');
   	                 $('.progressbar').css("width", percentage+'%');
   	                 $('.progressbar').html("Downloading CSV...   "+percentage+'%');
   	                  console.log(j_obj1);//return false;
   	                  if(percentage>95){
   	                	  //toastr.info("CSV Downloaded successfully");
   	                          clearInterval(myInterval);
   	                          $('#rcdivbar').addClass('hidden');
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
  $(document).ready(function() {
    	
        $('.hrefCall').click(function() {
        	 $('#popSample').addClass('is-visible');
            var validationCheck=$(this).attr('id');
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
                success: function(listOfValues) {
                    console.log(listOfValues);
                    if (typeof listOfValues !== "undefined" && listOfValues != "") {
                    	
                    	google.load("visualization", "1", {packages:["corechart"]});
                        google.setOnLoadCallback(drawChart);
                        var data1 = new google.visualization.DataTable();
                        data1.addColumn('string', 'Date (Run)');
              		    data1.addColumn('number', 'DQI');
              		 
              		  //data1.addRow([new Date("2016-02-04"),90]);
                        $.each(listOfValues, function(key, value) {	
                        	$.each( value, function( i, l ){
                        		 // alert( "Index #" + i + ": " + l );
        					data1.addRow([key,l]);
                        	});
        				});
                        data1.sort({column: 0, asc: true});
                        var title="";
                        if(validationCheck=="Aggregate DQI Summary"){
                        	title=validationCheck;
                        }else{
                        	title=validationCheck + " DQI";
                        }
                        var len = $.map(listOfValues, function(n, i) { return i; }).length;
                        console.log(len);
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
                            title: title,
                            hAxis: {title: 'Date (Run)',  titleTextStyle: {color: '#333'},direction:-1, slantedText:true, slantedTextAngle:45,showTextEvery:showTextEvery},
                            vAxis: {minValue: 0},
                            chartArea: {bottom: 100},
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
                error: function(xhr, textStatus,
                    errorThrown) {
                    $('#initial').hide();
                    $('#fail').show();
                }
            });
        });
        $( "#dqiDataCloseBtn" ).click(function() {
        	  $('#popSample').removeClass('is-visible');
        	});
    });
   	 
   	 
   	 </script>
</html>