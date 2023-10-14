
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>
<jsp:include page="checkVulnerability.jsp" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>	
	#ColNullSummaryTable a {
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
											style="border-top: 1px solid #ddd;border-bottom: 1px solid #ddd;">

										<li ><a onClick="validateHrefVulnerability(this)" 
												href="dashboard_table?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Count Reasonability</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="validity?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Microsegment Validity</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)"  href="dupstats?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Uniqueness</strong>
											</a></li>
											<li class="active" ><a onClick="validateHrefVulnerability(this)" 
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
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="recordAnomaly?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Record Anomaly</strong></a></li>
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
							
								<!-- Null Column Summary -->
							 
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Null Check Summary
									<a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${Null_Summary_TableName}','NullData')"  href="#"
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
							<input type="hidden" id="nullSummaryTableName" value="${Null_Summary_TableName}">
							<div class="portlet-body">
								<table id="ColNullSummaryTable"
									class="table table-striped table-bordered  table-hover"
									style="width: 100%;">
									<thead>
										<tr>
                                 			<th>Execution_Date</th>
											<th>Run</th>
											<th>Status</th>
											<th>Col_Name</th>
											<th>Null_Value</th>
											<th>Record_Count</th>
											<th>Null_Percentage</th>
											<th>Null_Threshold</th>
											<th>Learn</th>
											<th>Historic_Null_Mean</th>
											<th>Historic_Null_Stddev</th>
											<th>Historic_Null_Status</th>
											<th>Download File</th>
										</tr>
									</thead>
									
								</table>
							</div>
							
							
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Microsegment Null Check Summary
									<a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${ColSumm_TableName}','NullSumaryData')"  href="#"
										class="CSVStatus" id="">(Download CSV)</a></span>
								</div>
							</div>
							<hr />
							<input type="hidden" id="nullColSummaryTableName" value="${ColSumm_TableName}">
							<input type="hidden" id="idApp" value="${idApp}"> <input
								type="hidden" id="idData" value="${idData}">
							<div class="portlet-body">
								<table id="ColSummTable"
									class="table table-striped table-bordered  table-hover"
									style="width: 100%;">
									<thead>
										<tr>
											<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Date</th>
											<th>Run</th>
											<th>Status</th>
											<th>Col_Name</th>
											<th>Null_Value</th>
											<th>Record_Count</th>
											<th>Null_Percentage</th>
											<th>Null_Threshold</th>
											<th>Microsegment_Val</th>
											<th>Microsegment_Col</th>
										</tr>
									</thead>
									<%-- <tbody>
										<c:forEach var="data_Quality_Column_SummaryListobj"  items="${data_Quality_Column_Summary}">
											<c:forEach var="data_Quality_Column_Summaryobj"  items="${data_Quality_Column_SummaryListobj}">
											<tr>
												<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${data_Quality_Column_Summaryobj.date}</td>
												<td>${data_Quality_Column_Summaryobj.run}</td>
												<td><c:if
														test="${data_Quality_Column_Summaryobj.status eq 'passed'}">
														<span class="label label-success label-sm">${data_Quality_Column_Summaryobj.status}</span>
													</c:if> <c:if
														test="${data_Quality_Column_Summaryobj.status eq 'failed'}">
														<span class="label label-danger label-sm">${data_Quality_Column_Summaryobj.status}</span>
													</c:if></td>
													<td>${data_Quality_Column_Summaryobj.colName}</td>
												<td>${data_Quality_Column_Summaryobj.null_Value}</td>
												<td>${data_Quality_Column_Summaryobj.record_Count}</td>
												<td>${data_Quality_Column_Summaryobj.null_Percentage}</td>
												<td>${data_Quality_Column_Summaryobj.null_Threshold}</td>
												<td>${data_Quality_Column_Summaryobj.dGroupVal}</td>
												<td>${data_Quality_Column_Summaryobj.dGroupCol}</td>
											</tr>
											</c:forEach>
										</c:forEach>
									</tbody> --%>
								</table>
							</div>
							
							
						
							
							
								<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Default Value
									<a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${Default_value_TableName}','DefaultData')"   href="#"
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
							<input type="hidden" id="DefaultTableName"
								value="${Default_value_TableName}">
							<div class="portlet-body">
							
								<table id="ColSummDefaultTable"
									class="table table-striped table-bordered  table-hover"
									style="width: 100%;">
									<thead>
										<tr>
										     <th>Date</th>
											<th>Run</th>
											<th>Default_Col_Names</th>
											<th>Default_Values</th>
											<th>Default_Count</th>
											<th>Default_Percentage</th>
											<th>Download File</th>
										</tr>
									</thead>
									
								</table>
							</div>
						</div>
					</div>
					<!-- END EXAMPLE TABLE PORTLET-->
					
				</div>
			</div>
			<!-- close if -->
			<!-- close if -->
			
		</div>
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
			<br/><br/><br/><br/>
			<div id="chartDiv"
				style="height: 500px; width: 100%; margin: 0 auto;"
				class="dashboard-summary-chart"></div>
			<button type="submit" id="dqiDataCloseBtn" class="btn blue">Close</button>
			
		
		</div>
	</div>
	<jsp:include page="downloadCsvReports.jsp"/>
	<jsp:include page="footer.jsp" />
</body>

<!-- Load JWF framework module on this page -->	
<head>
	<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
	<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

<script>

/* jQuery(document).ready(function() {
	var table;
	console.log("hello");
	var LengthChktableName=$( "#LengthCheckTableName" ).val();
	var idApp=$( "#idApp" ).val();
	var idData=$( "#idData" ).val();
	//alert(LengthChktableName);
	if(LengthChktableName==$( "#LengthCheckTableName" ).val())
		{
	table = $('#lengthCheckTable').dataTable({
		  	"bPaginate": true,
		  	"order": [ 0, 'asc' ],
		  	"bInfo": true,
		  	"iDisplayStart":0,
		  	"bProcessing" : true,
		 	"bServerSide" : true,
		 	"dataSrc": "",
		 	'sScrollX' : true,
		 	"sAjaxSource" : path+"/lengthCheckTableName?tableName="+LengthChktableName,
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
}); */

jQuery(document).ready(function() {
	var table;
	console.log("hello");
	var tableName=$( "#nullColSummaryTableName" ).val();
	var idApp=$( "#idApp" ).val();
	var idData=$( "#idData" ).val();
	//alert(idData);
	table = $('#ColSummTable').dataTable({
		  	"bPaginate": true,
		  	"order": [ 0, 'asc' ],
		  	"bInfo": true,
		  	"iDisplayStart":0,
		  	"bProcessing" : true,
		 	"bServerSide" : true,
		 	"dataSrc": "",
		 	'sScrollX' : true,
		 	"sAjaxSource" : path+"/ColSummTableForNullTab?tableName="+tableName+"&idApp="+idApp,
		 	"aoColumns": [
                    {"data" : "Date"},
                    {"data" : "Run1"},
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
                    {"data" : "colName"},
                    {"data" : "Null_Value1"},
                    {"data" : "Record_Count1"},
                    {"data" : "Null_Percentage"},
                    {"data" : "Null_Threshold"},
                    {"data" : "DGroupVal"},
                    {"data" : "DGroupCol"}
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
});
jQuery(document).ready(function() {
	var table;
	console.log("hello");
	var tableName=$( "#nullSummaryTableName" ).val();
	var idApp=$( "#idApp" ).val();
	var idData=$( "#idData" ).val();
	
	initializeJwfSpaInfra();       // Load JWF framework module on this page
	
	table = $('#ColNullSummaryTable').dataTable({
		  	"bPaginate": true,
		  	"order": [ 0, 'asc' ],
		  	"bInfo": true,
		  	"iDisplayStart":0,
		  	"bProcessing" : true,
		 	"bServerSide" : true,
		 	"bFilter": true,
		 	"dataSrc": "",
		 	'sScrollX' : true,
		 	"sAjaxSource" : path+"/ColNullSummTableForNullTab?tableName="+tableName+"&idApp="+idApp,
		 	"aoColumns": [
		 	    {"data" : "Date"},
                {"data" : "Run1"},
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
                {"data" : "colName"},
                {"data" : "Null_Value1"},
                {"data" : "Record_Count"},
                {"data" : "Null_Percentage"},
                {"data" : "Null_Threshold"},
                {"data" : "Null_Percentage",
                  "render": function(data, type, row, meta){
                       var failPercentage = row.Null_Percentage;
                       nullColName = row.colName;
                       data = "<a onClick=\"javascript:updateColumnCheckThreshold("+idApp+",'Null Check','"+nullColName+"','"+failPercentage+"')\" class=\"fa fa-thumbs-down\"></a>";
                   return data;
                   }
                },
                {"data" : "Historic_Null_Mean"},
                {"data" : "Historic_Null_stddev"},
                {"data" : "Historic_Null_Status",
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
                {"data" : "colName",
                       "render": function(data, type, row, meta){
                          data = "<a onclick=\"javascript:downloadCsvReports('"+idApp+"','"+tableName+"','NullData/"+data+"')\" href=\"#\" style=\"font-size: small;\">Download</a>";
                           return data;
                       }
               }
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
});
jQuery(document).ready(function() {
	var table;
	console.log("hello");
	var tableName=$( "#DefaultTableName" ).val();
	var idApp=$( "#idApp" ).val();
	var idData=$( "#idData" ).val();
	table = $('#ColSummDefaultTable').dataTable({
		  	"bPaginate": true,
		  	"columnDefs": [
		  	     { orderable: false, targets: [6]}
		  	 		  	  ],
		
		  	"order": [ 0, 'asc' ],
		  	"bInfo": true,
		  	"iDisplayStart":0,
		  	"bProcessing" : true,
		 	"bServerSide" : true,
		 	"dataSrc": "",
		 	'sScrollX' : true,
		 	"sAjaxSource" : path+"/ColSummTableForDefaultValTab?tableName="+tableName+"&idApp="+idApp,
		 	"aoColumns": [
                    {"data" : "Date"},
                    {"data" : "Run1"},
                    {"data" : "colName"},
                    {"data" : "Default_Value1"},
                    {"data" : "Default_Count1"},
                    {"data" : "Default_Percentage1"},
                    {"data" : "colName",
                            "render": function(data, type, row, meta){
                               data = "<a onclick=\"javascript:downloadCsvReports('"+idApp+"','"+tableName+"','DefaultData/"+data+"')\" href=\"#\" style=\"font-size: small;\">Download</a>";
                                return data;
                            }
                    }

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
});
</script>
<script>
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
</html>
