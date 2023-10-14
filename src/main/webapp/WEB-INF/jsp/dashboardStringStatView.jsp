

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
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap.min.css">
<script
	src="./assets/global/plugins/bootstrap.min.js"></script>
<script
	src="./assets/global/plugins/jquery.min.js"></script>

<style>
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
  0% { -webkit-transform: rotate(0deg); }
  100% { -webkit-transform: rotate(360deg); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.px200 {
  width: 200px;
  max-width: 200px;
  word-wrap: break-word;
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
											<li class="active" ><a onClick="validateHrefVulnerability(this)" 
												href="stringstats?idApp=${idApp}" style="padding: 2px 2px;">
													<strong>Drift & Orphan</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="numericstats?idApp=${idApp}" style="padding: 2px 2px;">
													<strong>Distribution Check</strong>
											</a></li>
											<li ><a onClick="validateHrefVulnerability(this)" 
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
									<span class="caption-subject bold ">Data Drift Count Summary</span>
								</div>
							</div>
							<hr />
							<input type="hidden" id="tableNameOrphan" value="${DataDrift_Count_Summary_TableName}">
							<div class="portlet-body">
								<table id="ColSummTable"
									class="table table-striped table-bordered  table-hover "
									style="width: 100%;">
									<thead>
										<tr>
											<th>Date</th>
											<th>Run</th>
											<th>Col_Name</th>
											<th>Microsegment_Col</th>
											<th>Microsegment_Val</th>
											<th>Unique_Values_Count</th>
											<th>Missing_Values_Count</th>
											<th>New_Values_Count</th>
										</tr>
									</thead>
								</table>
							</div>

							<br /> <br /> <br />
							
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Data Drift Summary<a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${DataDrift_Summary_TableName}','DataDriftData')"
									 href="#"
									class="CSVStatus" id="">(Download CSV)</a></span>
								</div>
							</div>
							<hr />
						
							<div class="progress percentagebar hidden" id="datadriftdivbar">
								<div class="progress">
									<div class="progress-bar progressbar" role="progressbar"
										style="width: 5%;" aria-valuenow="100" aria-valuemin="0"
										aria-valuemax="100">5%</div>
								</div>
							</div>
							
							<input type="hidden" id="tableName3"
								value="${DataDrift_Summary_TableName}">
							 <div class="portlet-body">								
								<table id="DataDriftSummaryTable"
									class="table table-striped table-bordered  table-hover"
									style="width: 100%;">
									<thead>
										<tr>
											<%
												String summarydata = (String) request.getAttribute("dataDriftSummaryTableTrue");
												if (summarydata != null && summarydata.equals("dataDriftSummaryTableTrue")) {
											%>																				
											<th>Date</th>
											<th>Run</th>
											<th>Col_Name</th>
											<th>Unique_Values</th>
											<th>Operation</th>
											<th>Microsegment_Val</th>
											<th>Microsegment_Col</th>
											<th>User_Name</th>
											<th>Time</th>
											<th>Learn<a onClick="validateHrefVulnerability(this)"  href='rejectAll?rejectAll=True&tableName=${DataDrift_Summary_TableName}&tab=drift&idApp=${idApp}'>
										<b>(Reject All)</b>
									</a></th>
											<%
												}
											%>
										</tr>
									</thead>								
								</table>
							 </div>
							 
							 <div class="row" style="margin-left: 1%; margin-right: 1%;">
											<ul class="nav nav-tabs">
			<li class="active"><a onClick="validateHrefVulnerability(this)"  data-toggle="tab" href="#menu1" id="data-drift-tab">Data Drift</a></li>
		</ul>

		<div class="tab-content">
				 <!-- datadrift start -->
		 <div id="menu1" class="tab-pane fade in active">
		 <!-- sum of num stats tab-content code starts -->
		 <div class="row" style="margin-top: 5%">
		 <div class="col-sm-4">
      			<div class="dropdown">
				  <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Select Column Name
				  <span id="data-drift-colname-Span"><span class="caret"></span></span></button>
				  <ul class="dropdown-menu" id="data-drift-colname-dropdown-menu">
				  </ul>
				</div>
      		
      		</div>
      		<div class="col-sm-4">
			</div>
      		
      		<div class="col-sm-4"><button type="button" class="btn btn-primary" onclick="dataDriftCall()">Show Chart</button></div>
      	</div>
      	<div class="row" style="margin-top: 3%" >
      	<div class="col-sm-2"></div>
      	<div class="col-sm-8" id="data-drift-chart-headerid"></div>
      	<div class="col-sm-2"></div>
      	</div>
		 	<div class="row">
		 		<div id="data-drift-Chart-div"></div>
		 	</div>
		  <!-- sum of num stats tab-content code ends -->
		 </div>
		 <!-- data drift ends -->
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
<script type="text/javascript">
jQuery(document).ready(function() {
	var table;
	console.log("hello");
	var tableName=$( "#tableNameOrphan" ).val();
	var idApp=$( "#idApp" ).val();
	//alert(tableName);
	table = $('#ColSummTable').dataTable({
		  	"bPaginate": true,
		  	//"order": [ 1, 'asc' ],
		  	"order": [ 0, 'asc' ],
		  	"bInfo": true,
		  	"iDisplayStart":0,
		  	"bProcessing" : true,
		 	"bServerSide" : true,
		 	"dataSrc": "",
		 	'sScrollX' : true,
		 	"sAjaxSource" : path+"/ColSummTableForStringTab?tableName="+tableName+"&idApp="+idApp,
		 	"aoColumns": [

                {"data" : "Date"},
                {"data" : "Run1"},
                {"data" : "ColName"},
                {"data" : "dGroupCol"},
                {"data" : "dGroupVal"},
                {"data" : "uniqueValuesCount"},
                {"data" : "missingValueCount"},
                {"data" : "newValueCount"}

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
	      
	      tableName=$("#tableName2").val();
			//alert(tableName);
			console.log("tableName1");
			table = $('#DataDriftTable').dataTable({
		  	"bPaginate": true,
		  	"order": [ 0, 'asc' ],
		  	"bInfo": true,
		  	"iDisplayStart":0,
		  	"bProcessing" : true,
		 	"bServerSide" : true,
		 	"dataSrc": "",
		 	'sScrollX' : true,
		 	"sAjaxSource" : path+"/DataDriftTable?tableName="+tableName+"&idApp="+idApp,
		 	"aoColumns": [

                {"data" : "Date"},
                {"data" : "Run1"},
                {"data" : "ColName"},
                {"data" : "uniqueValuesCount"},
                {"data" : "missingValueCount"},
                {"data" : "newValueCount"}

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
			
		 tableName=$("#tableName3").val();				
			table = $('#DataDriftSummaryTable').dataTable({
		  	"bPaginate": true,
		  	"order": [ 0, 'asc' ],
		  	"bInfo": true,
		  	"iDisplayStart":0,
		  	"bProcessing" : true,
		 	"bServerSide" : true,
		 	"dataSrc": "",
		 	'sScrollX' : true,
		 	"sAjaxSource" : path+"/DataDriftSummaryTable?tableName="+tableName+"&idApp="+idApp,
		 	"aoColumns": [

                {"data" : "Date"},
                {"data" : "Run"},
                {"data" : "colName"},
                {"data" : "uniqueValues"},
                {"data" : "Operation"},
                {"data" : "dGroupVal"},
                {"data" : "dGroupCol"},
                {"data" : "userName"},
                {"data" : "Time"},
                {"data" : "status",
                "render": function(data, type, row, meta){
                     var colName =row.colName;
                     var uniqueValues = row.uniqueValues;
                     var dGroupVal =row.dGroupVal;
                     var dGroupCol =row.dGroupCol;
                     var Run = row.Run;
                     var operation= row.Operation;
                     if(operation == "Missing"){
                        data = "";
                     }
                     else if (data === "Rejected") {
                         data = "<font color='red'><b>Rejected  </b></font><a href='undoRejectInd?colName=" + colName + "&tab=drift&tableName=" + tableName
                                            + "&uniqueValues=" + uniqueValues + "&dGroupVal=" + dGroupVal + "&dGroupCol=" + dGroupCol + "&Run=" + Run + "&idApp="
                                            + idApp + "'>" + "<i class='fa fa-undo' style=\"font-size: 20px;\"></i></a>";
                     }
                     else{
                          data = "<a href='rejectInd?colName=" + colName + "&tab=drift&tableName=" + tableName
                                                + "&uniqueValues=" + uniqueValues + "&dGroupVal=" + dGroupVal + "&dGroupCol=" + dGroupCol + "&Run=" + Run + "&idApp="
                                                + idApp + "'>" + "<i class='fa fa-thumbs-down'></i></a>"
                                                + "<style>.fa { font-size: 26px;}</style>";
                    }

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
	
	
 <!-- this code for datadrift download csv bar -->
<script>

   	$( "#datadriftdiv" ).on( "click", function() {
   		console.log("datadriftdiv");
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
   	                 $('#datadriftdivbar').removeClass('hidden');
   	                 $('.progressbar').css("width", percentage+'%');
   	                 $('.progressbar').html("Downloading CSV...   "+percentage+'%');
   	                  console.log(j_obj1);//return false;
   	                  if(percentage>95){
   	                	  //toastr.info("CSV Downloaded successfully");
   	                          clearInterval(myInterval);
   	                          $('#datadriftdivbar').addClass('hidden');
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
   	 
  
  $(document).ready(function(){
	  dataDriftTabClick();
		  $("#data-drift-tab").click(function(){
			  dataDriftTabClick();
		  });
		});
	
  function dataDriftTabClick(){
	  console.log("dataDriftTabClick");
	  var idApp=$( "#idApp" ).val();
	  var tablename = 'DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY';
		console.log('table name : '+tablename);
		if (tablename) {
			$(function() {
				var form_data = {
					tableName : tablename,
					idApp : idApp
				};
				var request = $.ajax({
					url : './getListOfColName',
					type : 'POST',
					headers: { 'token':$("#token").val()},
					data : form_data,
					dataType : 'json'
				});
				request.done(function(msg) {
					console.log('success : ');
					console.log(msg);
					bindColumnListToDataDriftDropDown(msg);
				});
				request.fail(function(jqXHR, textStatus) {
					console.log("comparision chart dropdown Request failed: "+ textStatus);
				});

			});
		}
  }
  
	function bindColumnListToDataDriftDropDown(obj){
		/*bind dgroupval*/
		var arrayLength = obj.length;
		$("#data-drift-colname-dropdown-menu").empty();
		for (var i = 0; i < arrayLength; i++) {
			$("#data-drift-colname-dropdown-menu").append("<li id=''><a onClick='validateHrefVulnerability(this);dataDriftChartBindValueToVariable(this)' >"+ obj[i] + "</a></li>");
		}
	}
	
	var dataDriftChartSelectedColumnValue;
	
	function dataDriftChartBindValueToVariable(param){
		document.getElementById("data-drift-colname-Span").innerHTML = "";
		dataDriftChartSelectedColumnValue = $(param).text();
		document.getElementById("data-drift-colname-Span").innerHTML = "["+ dataDriftChartSelectedColumnValue + "]";
	}
	
	function dataDriftCall(){
		console.log('sumOfNumStatsChartDropdownMenuValueSection started');
		console.log(dataDriftChartSelectedColumnValue);
		
		document.getElementById("data-drift-chart-headerid").innerHTML = "";
		document.getElementById("data-drift-chart-headerid").innerHTML = "<div class='loader' style='margin-left: 50%;'></div>";
	
		 var idApp=$( "#idApp" ).val();
		  var tablename = 'DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY';
		if (tablename && dataDriftChartSelectedColumnValue) {
			console.log("ajax call");
			sumOfNumStatsRunValue = '0';
			$(function() {
				var form_data = {
					tableName : tablename,
					colName : dataDriftChartSelectedColumnValue,
					idApp : idApp
				};
				var request = $.ajax({
					url : './datadriftChart',
					type : 'POST',
					headers: { 'token':$("#token").val()},
					data : form_data,
					dataType : 'json'
				});
				request.done(function(msg) {
							sumOfNumStatsRunValue = '0';
							drawDataDriftChart(msg); //, 'Date', 'Sum Of Num Stat'
							document.getElementById('data-drift-chart-headerid').innerHTML = '<h5><b> Data Drift Chart with Column Name : '+ dataDriftChartSelectedColumnValue+ ' </b></h5>';
						});
				request.fail(function(jqXHR, textStatus) {
					sumOfNumStatsRunValue = '0';
					console.log("Request failed: " + textStatus);
				});
			});
		} else {
			document.getElementById("data-drift-chart-headerid").innerHTML = "<div class='alert alert-danger'> <strong>Error!</strong> Select value from dropdowns </div>";
		}
	}
	
	
	function drawDataDriftChart(obj){
		var dataDriftObj = obj.dataDrift;
		var data = new google.visualization.DataTable();
		data.addColumn('string', 'Date & Run');
		data.addColumn('number', 'Unique Value');
		data.addColumn('number', 'Missing Value');
		data.addColumn('number', 'New Value');
		data.addRows(dataDriftObj);
		
		 data.sort({
	         column: 0,
	         desc: true
	       });
		
		var options = {
			title : '',
			vAxis : {
				title : 'Count'
			},
			hAxis : {
				title : 'Date & Run',direction:-1, slantedText:true, slantedTextAngle:45
			},
		/*	seriesType : 'line',
			series : {
				1 : {
					color : 'black',
					lineWidth : 0,
					pointSize : 5
				}
			}, */
			 chartArea:{left:80,top:10,width:'70%'},
			width : 1000,
			height : 400,
			isStacked: true
		};

		var chart = new google.visualization.ComboChart(document.getElementById('data-drift-Chart-div'));
		chart.draw(data, options);
	}
   	 </script>
</html>