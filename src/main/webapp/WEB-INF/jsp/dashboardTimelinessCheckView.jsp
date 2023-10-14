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
											<li ><a onClick="validateHrefVulnerability(this)" 
												href="recordAnomaly?idApp=${idApp}"
												style="padding: 2px 2px;"> <strong>Record
														Anomaly</strong></a></li>
											<li class="active" ><a onClick="validateHrefVulnerability(this)" 
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
									<span class="caption-subject bold ">Timeliness Check
										Summary <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','${timeliness_summary_TableName}','TimelinessData')"
										 href="#"
										class="CSVStatus" id="">(Download CSV)</a>
									</span>
								</div>
							</div>
							<hr />
							<input type="hidden" id="tableName"
								value="${timeliness_summary_TableName}">

							<div class="portlet-body">
								<table id="TimelinessCheckSummaryTable"
									class="table table-striped table-bordered  table-hover"
									style="width: 100%;">
									<thead>
										<tr>
											<th>Date</th>
											<th>Run</th>
											<th>S_Date</th>
											<th>E_Date</th>
											<th>Timeliness_Key</th>
											<th>No_Of_Days</th>
											<th>Status</th>
										</tr>
									</thead>
								</table>
							</div>
							<br />
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

<script>
				var table;

				tableName=$("#tableName").val();
				var idApp = ${idApp};
				//alert(tableName);
				console.log(tableName);
				table = $('#TimelinessCheckSummaryTable').dataTable({
					  	"bPaginate": true,
					  	"order": [ 0, 'asc' ],
					  	"bInfo": true,
					  	"iDisplayStart":0,
					  	"bProcessing" : true,
					 	"bServerSide" : true,
					 	"dataSrc": "",
					 	'sScrollX' : true,
					 	"sAjaxSource" : path+"/TranDetailIdentityTable?tableName="+tableName+"&idApp="+idApp,
					 	"aoColumns": [

                                {"data" : "Date"},
                                {"data" : "Run"},
                                {"data" : "SDate"},
                                {"data" : "EDate"},
                                {"data" : "TimelinessKey"},
                                {"data" : "No_Of_Days"},
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
                                }
                         ],
					 	"dom": 'C<"clear">lfrtip',
						colVis: {
							"align": "left",
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