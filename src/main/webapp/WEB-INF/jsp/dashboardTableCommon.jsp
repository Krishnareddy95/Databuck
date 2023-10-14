<%@page import="java.util.ArrayList"%>
<%@page import="com.databuck.bean.ListApplications"%>
<%@page import="com.databuck.bean.DATA_QUALITY_Transactionset_sum_A1"%>
<%@page import="java.util.List"%>
<%@page import="java.text.DecimalFormat"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DataBuck</title>
</head>
<body>

	<jsp:include page="viewDatatable.jsp" />
	<jsp:include page="header.jsp" />
	<jsp:include page="container.jsp" />
	<jsp:include page="checkVulnerability.jsp" />
	<style>
#right-href {
	float: right;
}

#g1, #g2, #g3, #g4, #g5, #g6, #g7, #g8, #g9, #g10, #g11, #g17, #g18,
	#g16, #g19, #g20, #g21, #g22, #g23, #g24, #g25 {
	width: 100px;
	height: 100px;
	display: inline-block;
	margin: -1em;
}

@
-webkit-keyframes blinker {
	from {opacity: 1.0;
}

to {
	opacity: 0.0;
}

}
.blink {
	text-decoration: blink;
	-webkit-animation-name: blinker;
	-webkit-animation-duration: 0.9s;
	-webkit-animation-iteration-count: infinite;
	-webkit-animation-timing-function: ease-in-out;
	-webkit-animation-direction: alternate;
}

.tooltip-inner {
	background-color: #7ec7d9 !important;
	color: black !important;
	padding: 15px;
	font-size: 15px;
}

.display-content-center{
display:flex;
justify-content: center;

}
.text-center-align{
text-align: center;
}
.height-130{
height:130px;
}
.height-30{
height:30px;
}

.flex-container {
  display: flex;
  padding: 2% 0%;

}
.flex-container > div {
  margin: auto;
}

</style>
	<script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
	<script type="text/javascript" src="./assets/guage/justgage.js"></script>
	<script type="text/javascript"
		src="./assets/guage/raphael-2.1.4.min.js"></script>
	<script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet"
		href="./assets/global/plugins/bootstrap.min.css">
	<script
		src="./assets/global/plugins/bootstrap.min.js"></script>
	<script
		src="./assets/global/plugins/jquery.min.js"></script>
	<script type="text/javascript"
		src="./assets/global/plugins/loader.js"></script>

	<script type="text/javascript">

      google.load("visualization", "1", {"packages":["corechart"]});
      google.setOnLoadCallback(drawChart);

      function drawChart() {

	     /* var data = google.visualization.arrayToDataTable([
	          ['Date', 'Record Count Anomaly'],
	   	  <c:forEach var="recordCountAnomalyGraphValuesobj" items="${recordCountAnomalyGraphValues}">
	          ['${recordCountAnomalyGraphValuesobj.date}',  ${recordCountAnomalyGraphValuesobj.recordCount}],
		  </c:forEach>
	       ]);

	      var options = {
	          title: 'Summary - Record Count',
	          hAxis: {title: 'Date',  titleTextStyle: {color: '#333'}},
	          vAxis: {minValue: 0},
	          height: 250,
	          width: 1000
	      };

	      var chart = new google.visualization.AreaChart(document.getElementById('record_summary_div'));
	      chart.draw(data, options);

      var data = google.visualization.arrayToDataTable([
        ['Date', 'Null Count'],
        <c:forEach var="nullCountGraphValuesobj" items="${nullCountGraphValues}">
        ['${nullCountGraphValuesobj.date}',  ${nullCountGraphValuesobj.totalCount}],
	</c:forEach>

      ]);

      var options = {
          title: 'Summary - Null Count',
          hAxis: {title: 'Date',  titleTextStyle: {color: '#333'}},
          vAxis: {minValue: 0},
          height: 250,
          width: 1000
      };

      var chart = new google.visualization.AreaChart(document.getElementById('null_summary_div'));
      chart.draw(data, options);

      //length check---

       var data = google.visualization.arrayToDataTable([
        ['Date', 'Length Check'],
        <c:forEach var="lengthCheckGraphValuesobj" items="${lengthValueValues}">
        ['${lengthCheckGraphValuesobj.date}',  ${lengthCheckGraphValuesobj.totalCount}],
	</c:forEach>

      ]);

      var options = {
          title: 'Summary - Null Count',
          hAxis: {title: 'Date',  titleTextStyle: {color: '#333'}},
          vAxis: {minValue: 0},
          height: 250,
          width: 1000
      };

      var chart = new google.visualization.AreaChart(document.getElementById('Length_Check_div'));
      chart.draw(data, options);

      //---------------

      //

      ---

      /*  var data = google.visualization.arrayToDataTable([
        ['Date', 'Bad Data'],
        <c:forEach var="badDataGraphValuesobj" items="${badDataValueValues}">
        ['${badDataGraphValuesobj.date}',  ${badDataGraphValuesobj.totalCount}],
	</c:forEach>

      ]);

      var options = {
          title: 'Summary - Bad Data',
          hAxis: {title: 'Date',  titleTextStyle: {color: '#333'}},
          vAxis: {minValue: 0},
          height: 250,
          width: 1000
      };

      var chart = new google.visualization.AreaChart(document.getElementById('Bad_Data_div'));
      chart.draw(data, options); */

      //---------------

    /*  var data = google.visualization.arrayToDataTable([
        ['Date', 'Duplicates'],
        <c:forEach var="allfieldsGraphobj" items="${allfieldsGraph}">
        ['${allfieldsGraphobj.date}',  ${ allfieldsGraphobj.duplicate }],	</c:forEach>
      ]);

      var options = {
          title: 'Summary - All fields - duplicate rows',
          hAxis: {title: 'Date',  titleTextStyle: {color: '#333'}},
          vAxis: {minValue: 0},
          height: 250,
          width: 1000
      };

      var chart = new google.visualization.AreaChart(document.getElementById('all_dup_summary_div'));
      chart.draw(data, options);

      var data = google.visualization.arrayToDataTable([
        ['Date', 'Duplicates'],
        <c:forEach var="identityfieldsGraphobj" items="${identityfieldsGraph}">
        ['${identityfieldsGraphobj.date}',  ${identityfieldsGraphobj.duplicate}],
	</c:forEach>
      ]);

      var options = {
          title: 'Summary - Identity fields - duplicate rows',
          hAxis: {title: 'Date',  titleTextStyle: {color: '#333'}},
          vAxis: {minValue: 0},
          height: 250,
          width: 1000
      };

      var chart = new google.visualization.AreaChart(document.getElementById('id_dup_summary_div'));
      chart.draw(data, options);

      var data = google.visualization.arrayToDataTable([
        ['Date', 'Numerical'],
        <c:forEach var="numericalFieldStatsGraphobj" items="${numericalFieldStatsGraph}">
        ['${numericalFieldStatsGraphobj.date}',  ${numericalFieldStatsGraphobj.count}],
	</c:forEach>
      ]);

      var options = {
          title: 'Summary - Numerical Field Stats',
          hAxis: {title: 'Date',  titleTextStyle: {color: '#333'}},
          vAxis: {minValue: 0},
          height: 250,
          width: 1000
      };

      var chart = new google.visualization.AreaChart(document.getElementById('num_summary_div'));
      chart.draw(data, options);

      var data = google.visualization.arrayToDataTable([
        ['Date', 'String'],
        <c:forEach var="stringFieldStatsGraphobj" items="${stringFieldStatsGraph}">
        ['${stringFieldStatsGraphobj.date}',  ${stringFieldStatsGraphobj.count}],
	</c:forEach>
      ]);

      var options = {
          title: 'Summary - String Field Stats',
          hAxis: {title: 'Date',  titleTextStyle: {color: '#333'}},
          vAxis: {minValue: 0},
          height: 250,
          width: 1000
      };

      var chart = new google.visualization.AreaChart(document.getElementById('str_summary_div'));
      chart.draw(data, options);

      var data = google.visualization.arrayToDataTable([
          ['Date', 'Record Anomaly'],
          <c:forEach var="recordAnomalyGraphobj" items="${recordAnomalyGraph}">
          ['${recordAnomalyGraphobj.date}',  ${recordAnomalyGraphobj.count}],
	</c:forEach>
        ]);

        var options = {
            title: 'Summary - Record Anomaly',
            hAxis: {title: 'Date',  titleTextStyle: {color: '#333'}},
            vAxis: {minValue: 0},
            height: 250,
            width: 1000
        };

        var chart = new google.visualization.AreaChart(document.getElementById('recordanomaly_summary_div'));
        chart.draw(data, options);*/


        /* ----------------------Gauge Chart-------------------  */





    	 /*        var data = google.visualization.arrayToDataTable([
    	          ['Label', 'Value'],
    	          ['Memory', 80],
    	          ['CPU', 55],
    	          ['Network', 68]
    	        ]);

    	        var options = {
    	          width: 400, height: 120,
    	          redFrom: 90, redTo: 100,
    	          yellowFrom:75, yellowTo: 90,
    	          minorTicks: 5
    	        };

    	        var chart = new google.visualization.Gauge(document.getElementById('chart_div'));

    	        chart.draw(data, options);

    	        setInterval(function() {
    	          data.setValue(0, 1, 40 + Math.round(60 * Math.random()));
    	          chart.draw(data, options);
    	        }, 13000);
    	        setInterval(function() {
    	          data.setValue(1, 1, 40 + Math.round(60 * Math.random()));
    	          chart.draw(data, options);
    	        }, 5000);
    	        setInterval(function() {
    	          data.setValue(2, 1, 60 + Math.round(20 * Math.random()));
    	          chart.draw(data, options);
    	        }, 26000); */



    }
    </script>



	<script>
		var ManageDateRange = {
			DateRange: { "StartDate": "", "EndDate": "", "IsDownloadCsvDateRangeEnabled": "N" },
			storeInitialDates: function(sIsDownloadCsvDateRangeEnabled) {
				var sStartDate = $('input[data-fromdate="Y"]').val(), sEndDate = $('input[data-todate="Y"]').val();
				ManageDateRange.DateRange.StartDate = sStartDate;
				ManageDateRange.DateRange.EndDate = sEndDate;
				ManageDateRange.DateRange.IsDownloadCsvDateRangeEnabled = sIsDownloadCsvDateRangeEnabled;

				console.log('storeInitialDates = ' + JSON.stringify(ManageDateRange.DateRange));
			},
			storeChangedDates: function(oDateInput, nWhichDate) {
				if (nWhichDate === 1) {
					ManageDateRange.DateRange.StartDate = oDateInput.value;
				} else {
					ManageDateRange.DateRange.EndDate = oDateInput.value;
				}
				console.log('storeChangedDates = ' + JSON.stringify(ManageDateRange.DateRange));
			}
		}

		var oApprovalStatusObject = {};

      $(document).ready(function () {
        var sApprovalStatusObject = '${approvalStatusFlag}';
        var oImgObject = $('#ReviewProcessIcon')[0];
        var sSrcImg = './assets/img/{img}';
        var oStrObject = $('#ReviewProcessString')[0];
        var srcString = '';

        var sIsDownloadCsvDateRangeEnabled = '${IsDownloadCsvDateRangeEnabled}';

        ManageDateRange.storeInitialDates(sIsDownloadCsvDateRangeEnabled);

        oApprovalStatusObject = JSON.parse(sApprovalStatusObject);
        srcString =
          oApprovalStatusObject.element_reference === 'NOT_STARTED'
            ? 'not_viewed_icon.jpg'
            : oApprovalStatusObject.element_reference === 'REVIEWED'
            ? 'reviewed_icon.jpg'
            : oApprovalStatusObject.element_reference === 'APPROVED'
            ? 'approved_icon.jpg'
            : 'not_viewed_icon.jpg';
        sSrcImg = sSrcImg.replace('{img}', srcString);
        oImgObject.setAttribute('src', sSrcImg);
        oStrObject.innerHTML = oApprovalStatusObject.element_text;
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

                  console.log("listOfValues---->>>>> :"+listOfValues);
                  if (typeof listOfValues !== "undefined" && listOfValues != "") {


                	  google.charts.load('current', {'packages':['gauge']});
                   	  google.charts.setOnLoadCallback(drawChart);

                   	google.charts.load('current', {
                   	    packages: ['controls', 'corechart']
                   	});

                 //	  google.load("visualization", "1", {packages:["corechart"]});
                      //google.setOnLoadCallback(drawChart);
                      var data11 = new google.visualization.DataTable();

      				data11.addColumn('string', 'Date(Run)');
      				data11.addColumn('number', 'DQI');

                    /*   data1.addColumn('string', 'Date(Run)');
            		  data1.addColumn('number', 'DQI'); */

            		 // data1.addRow([new Date("2016-02-04"),90]);

                      $.each(listOfValues, function(key, value) {
                      	$.each( value, function( i, l ){
                      		 // alert( "Index #" + i + ": " + l );
      					data11.addRow([key,l]);
                      	});
      				});
                      data11.sort({column: 0, asc: true});
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
                          hAxis: {title: 'Date (Run)',  titleTextStyle: {color: '#333'},slantedText:true, slantedTextAngle:45,showTextEvery:showTextEvery},
                          vAxis: {minValue: 0},
                          chartArea: {bottom: 100},
                          height: 400,
                          width: 1000
                      };
                      $('#loading-data-progress').addClass('hidden');
                      var chart = new google.visualization.AreaChart(document.getElementById('chartDiv'));
                      chart.clear();
                      chart.draw(data11, options);


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




	<div class="page-content-wrapper">
		<!-- BEGIN CONTENT BODY -->
		<div class="page-content">
			<!-- BEGIN PAGE TITLE-->
			<!-- END PAGE TITLE-->
			<!-- END PAGE HEADER-->
			<!-- BEGIN PAGE BASE CONTENT -->
			<!-- <div class="page-head"> -->
			<!-- BEGIN PAGE TITLE -->
			<!-- <div class="page-title" >
                            <h1> Dashboard </h1>
                        </div> -->
			<!-- </div> -->
			<div class="row">
				<div class="col-md-12">
					<!-- BEGIN EXAMPLE TABLE PORTLET-->
					<div class="portlet light bordered">
						<div class="portlet-title">
							<div class="caption font-red-sunglo">
								<span class="caption-subject bold ">Summary of Last Run (
									${dateForDashboard} ) for Validation Check : ${applicationName}
									&nbsp(Project: ${projectName})
								</span>
							</div>

							<span class="caption-subject bold" style="float: right; padding-left:15px;">
								<a onClick="validateHrefVulnerability(this);javascript:downloadDQSummaryReport('${idApp}')"  href="#" class="CSVStatus" id="">
									<i class="fa fa-download" aria-hidden="true"></i> Download Summary
								</a>
							</span>
							<span class="caption-subject bold" style="float: right;"><a onClick="validateHrefVulnerability(this)"
								href="downloadJSONObject?idApp=${idApp}"><i
									class="fa fa-download" aria-hidden="true"></i> Download DQI
									JSON</a></span>
						</div>


						<div class="flex-container">

							<div>
								<input type="hidden" id=advancedRulesListcount
									value="${advancedRulesListcount}">
								<table>
									<thead>
										<tr class="height-130">
											<td class="display-content-center"><span
												class="caption-subject bold"> <a onClick="validateHrefVulnerability(this)"
													href="advancedRulesListView?idData=${idData}&templateName=${templateName}"
													target="_blank" style="color: black"> <img
														style="height: 120px;" alt="Qries"
														src="./assets/img/advance_rules.jpg" alt="logo"
														opacity="0.5" filter="alpha(opacity=50)";>
												</a>
											</span></td>
										</tr>
										<tr class ="height-30">
											<td class="text-center-align"><a onClick="validateHrefVulnerability(this)"
												href="advancedRulesListView?idData=${idData}&templateName=${templateName}"
												target="_blank" style="color: black"> <strong>
														Advance Rules - ${advancedRulesListcount} Rules Discovered</strong>
											</a></td>

										</tr>



									</thead>
								</table>
							</div>


							<div>
								<table>
									<thead>
										<tr class="height-130">

											<td class="display-content-center"><div id="AggDqiChart_div"></div></td>
											<td><a onClick="validateHrefVulnerability(this)"  class="hrefCall" id="Aggregate DQI Summary"><i
													class="fa fa-area-chart" style="font-size: 20px"></i></a></td>
										</tr>

										<tr class="height-30">

											<td class="text-center-align">
											<strong> Aggregate
													DQI </strong></td>


										</tr>
									</thead>
								</table>
							</div>




							<div>

								<table>
									<thead>
										<tr class="height-130">
											<td class="display-content-center"><span class="caption-subject bold"> <a onClick="validateHrefVulnerability(this)"
													href="dataProfiling_View?idData=${idData}" target="_blank"
													style="color: black"> <img alt="Qries"
														src="./assets/img/deep_profiling.jpg" alt="logo"
														opacity="0.5" filter="alpha(opacity=50)";>
												</a></span></td>


										</tr>

										<tr class="height-30">
											<td class="text-center-align"><a onClick="validateHrefVulnerability(this)"  href="dataProfiling_View?idData=${idData}"
												target="_blank" style="color: black"> <strong>
														Deep Profiling </strong>
											</a></td>


										</tr>
									</thead>
								</table>
							</div>

							<div>
								<table >
									<thead>
										<tr class="height-130">
											<td class="display-content-center"><span class="caption-subject bold"> <a onClick="validateHrefVulnerability(this)"
													href="reviewProcessController?idApp=${idApp}"
													style="color: black"> <img
														style="height: 110px; width: 110px"
														src=""
														id="ReviewProcessIcon"
														alt="logo" opacity="0.5" filter="alpha(opacity=50)";>
												</a></span></td>

										</tr>
										<tr class="height-30">
											<td class="text-center-align"><a onClick="validateHrefVulnerability(this)"  href="reviewProcessController?idApp=${idApp}"
												style="color: black"><strong id="ReviewProcessString">
														</strong>

											</a></td>


										</tr>
									</thead>
								</table>
							</div>

					<c:if test="${isRuleCatalogDiscovery}">
							<div>

								<table>
									<thead>
										<tr class="height-130">
											<td class="display-content-center"><span class="caption-subject bold"> <a onClick="validateHrefVulnerability(this)"
													href="getRuleCatalog?idApp=${idApp}&fromMapping=${fromMapping}"
													style="color: black"> <img alt="Qries"
														src="./assets/img/ruleCatalogapproval.png" alt="logo"
														opacity="0.5" filter="alpha(opacity=50)";>
												</a></span></td>


										</tr>

										<tr class="height-30">
											<td class="text-center-align"><a onClick="validateHrefVulnerability(this)"  href="getRuleCatalog?idApp=${idApp}&fromMapping=${dashboardTableView}"
												 style="color: black"> <strong>
														Rule Catalog Approval</strong>
											</a></td>


										</tr>
									</thead>
								</table>
							</div>
					</c:if>


							<!-- 	<div class="col-md-4 col-capture"></div>
							<div class="col-md-4 col-capture"></div>
							<div class="col-md-4 col-capture"></div>
 -->
							<%-- 	<div class="col-md-4 col-capture">
								<span class="caption-subject bold" style="float: right;"><a onClick="validateHrefVulnerability(this)"
								href="advancedRulesListView?idData=${idData}&templateName=${templateName}" target="_blank">Advanced Rules</a></span>
							</div> --%>
							<!-- <div class="col-md-4 col-capturAggregate DQI Summarye"></div> -->

						</div>


						<div class="portlet-body">
							<input type="hidden" id="idApp" value="${idApp}"> <input
								type="hidden" id="tourl" value="">

							<div class="portlet-body">
								<table
									class="table table-striped table-bordered  table-hover"
									id="dashboardTable" style="width: 100%;">
									<thead>
										<tr>
											<th>Test</th>
											<th>Data Quality Index</th>
											<th>Status</th>
											<th>Key Metric-1</th>
											<th>Measurement</th>
											<th>Key Metric-2</th>
											<th>Measurement</th>

											<!-- <th>Summary</th> -->
										</tr>
									</thead>
									<tbody>

										<%-- <c:forEach items="${fileNameandcolumnOrderStatus}"
											var="fileNameandcolumnOrderStatusobj">
											<tr>
												<td class="hidden-xs">File Content Validation</td>
												<td class="hidden-xs"></td>
												<td><c:if
														test="${fileNameandcolumnOrderStatusobj.fileNameValidationStatus eq 'passed'}">
														<span class="label label-success label-sm">${fileNameandcolumnOrderStatusobj.fileNameValidationStatus}</span>
													</c:if> <c:if
														test="${fileNameandcolumnOrderStatusobj.fileNameValidationStatus eq 'failed'}">
														<span class="label label-danger label-sm">${fileNameandcolumnOrderStatusobj.fileNameValidationStatus}</span>
													</c:if></td>
												<td class="hidden-xs"></td>
												<td class="hidden-xs"></td>
												<td class="hidden-xs"></td>
												<td class="hidden-xs"></td>

												<!-- <td class="hidden-xs"></td> -->
											</tr>
											<tr>
												<td class="hidden-xs">Column Order Validation</td>
												<td class="hidden-xs"></td>
												<td><c:if
														test="${fileNameandcolumnOrderStatusobj.columnOrderValidationStatus eq 'passed'}">
														<span class="label label-success label-sm">${fileNameandcolumnOrderStatusobj.columnOrderValidationStatus}</span>
													</c:if> <c:if
														test="${fileNameandcolumnOrderStatusobj.columnOrderValidationStatus eq 'failed'}">
														<span class="label label-danger label-sm">${fileNameandcolumnOrderStatusobj.columnOrderValidationStatus}</span>
													</c:if></td>
												<td class="hidden-xs"></td>
												<td class="hidden-xs"></td>
												<td class="hidden-xs"></td>
												<td class="hidden-xs"></td>

												<!-- <td class="hidden-xs"></td> -->
											</tr>
										</c:forEach> --%>
										<%
											try {
											List<DATA_QUALITY_Transactionset_sum_A1> fileNameandcolumnOrderStatus = (List) request
											.getAttribute("fileNameandcolumnOrderStatus");
											List<DATA_QUALITY_Transactionset_sum_A1> fileNameandcolumnOrderStatusDummy = fileNameandcolumnOrderStatus;
											if (fileNameandcolumnOrderStatus.size() == 0) {
												fileNameandcolumnOrderStatus = new ArrayList<DATA_QUALITY_Transactionset_sum_A1>();
												DATA_QUALITY_Transactionset_sum_A1 dq = new DATA_QUALITY_Transactionset_sum_A1();
												dq.setFileNameValidationStatus("");
												dq.setColumnOrderValidationStatus("");
												fileNameandcolumnOrderStatus.add(dq);

											}
											if (true) {
										%>
										<%
											Object obj = request.getAttribute("listApplicationsData");
										ListApplications listapplications = (ListApplications) obj;
										//out.println(la);
										%>
										<%
											if (fileNameandcolumnOrderStatusDummy.size() != 0) {
										%>

										<tr>
											<!-- <td class="hidden-xs">Record Count Reasonability</td> -->
											<td class="hidden-xs"><a onClick="validateHrefVulnerability(this)"
														href="dashboard_table?idApp=${idApp}">Record Count Reasonability
													</a></td>
											<td class="hidden-xs" style="width: 175px;">
												<%
													if (request.getAttribute("showDQIEmpty") != null) {
													Double recordAnomalyScore = Double.valueOf(request.getAttribute("recordAnomalyScore").toString());
													Object rcaStatus = request.getAttribute("RCAStatus");

													if(rcaStatus != null && !rcaStatus.toString().trim().isEmpty()){
															if (recordAnomalyScore <= 0) {
												%>

												<div class="row">
													<div class="col-md-6">
														<div
															class="caption font-red-sunglo caption-subject bold blink"
															style="text-align: center; font-size: 130%">${recordAnomalyScore}</div>
													</div>
													<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Record Count Anomaly"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	} else {
 %>

												<div class="row">
													<div class="col-md-6">
														<div id="g1"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Record Count Anomaly"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
  }
 }
 %>
											</td>
											<td><c:if test="${RCAStatus eq 'passed'}">
													<span class="label label-success label-sm">${RCAStatus}</span>
												</c:if> <c:if test="${RCAStatus eq 'failed'}">
													<span class="label label-danger label-sm">${RCAStatus}</span>
												</c:if></td>
											<%-- <td class="hidden-xs">Record Count</td>
											<td class="hidden-xs text-center">${RCAKey_Matric_1}</td> --%>
											<%-- ${RCAKey_Matric_1} --%>
											<%
												DecimalFormat df = new DecimalFormat(",###");
											%>
											<%-- 	<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(valMat1))%></td> --%>

											<td class="hidden-xs">Record Count</td>
											<c:choose>
												<c:when test="${RCAKey_Matric_1 eq null}">
													<td class="hidden-xs text-center">${RCAKey_Matric_1}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="dashboard_table?idApp=${idApp}"> <strong>
																<%=df.format(Double.parseDouble(request.getAttribute("RCAKey_Matric_1").toString()))%>
														</strong>
													</a></td>
												</c:otherwise>
											</c:choose>

											<!-- Remove row name and value  -->
											<td class="hidden-xs"></td>
											<td class="hidden-xs text-center"></td>
											<%-- <td class="hidden-xs">No. of Microsegment Failed</td>
											<td class="hidden-xs text-center">${RCAKey_Matric_2}</td> --%>

										</tr>

										<%
											}
										%>

										<!-- --------length Check/Max Length Check------------ -->
										<%
											DecimalFormat df = new DecimalFormat(",###");
										System.out.println("Length Check====================");
										if (!listapplications.getlengthCheck().equalsIgnoreCase("Y") && listapplications.getMaxLengthCheck().equalsIgnoreCase("Y")) {
										%>
										<tr>
                      <td class="hidden-xs"><a
                            href="badData?idApp=${idApp}" data-toggle="tooltip"
                            data-html="true" title="${MaxLengthKey_Matric_3}"
                            data-placement="right">Length Check (Conformity)
                          </a></td>
                      <td class="hidden-xs">
                        <%
                          System.out.println("========maxLenCheckScore=="+request.getAttribute("lengthCheckScore"));
                          if (request.getAttribute("lengthCheckScore") != null) {
                          Double maxLengthCheckScore = Double.valueOf(request.getAttribute("lengthCheckScore").toString());
                          //Double maxLengthCheckScore = 60.0;

                          System.out.println("Double maxLengthCheckScore =>"+maxLengthCheckScore);
                          if (maxLengthCheckScore <= 0) {
                        %>
                        <div class="row">
                          <div class="col-md-6">
                            <div
                              class="caption font-red-sunglo caption-subject bold blink"
                              style="text-align: center; font-size: 130%">${lengthCheckScore}</div>
                          </div>
                          <div class="col-md-6">
                            &nbsp&nbsp&nbsp&nbsp&nbsp<a class="hrefCall"
                              id="Max Length Check"><i class="fa fa-area-chart"
                              style="font-size: 20px"></i></a>
                          </div>
                        </div> <%
  } else {
 %>
                        <div class="row">
                          <div class="col-md-6">
                            <div id="g24"></div>
                          </div>
                          <div class="col-md-6">
                            <br /> <br />&nbsp&nbsp&nbsp&nbsp <a class="hrefCall"
                              id="Max Length Check"><i class="fa fa-area-chart"
                              style="font-size: 20px"></i></a>
                          </div>
                        </div> <%
  }
 }
 %>
                      </td>
                      <td><c:if test="${MaxLengthStatus eq 'passed'}">
                          <span class="label label-success label-sm">${MaxLengthStatus}</span>
                        </c:if> <c:if test="${MaxLengthStatus eq 'failed'}">

                          <span class="label label-danger label-sm">${MaxLengthStatus}</span>
                        </c:if></td>
                      <%
                        System.out.println("============================Data Completeness");
                      %>


                      <td class="hidden-xs">Number of Columns Tested</td>
                      <c:choose>
                        <c:when test="${MaxLengthKey_Matric_1 eq null}">
                          <td class="hidden-xs text-center">${MaxLengthKey_Matric_1}</td>
                        </c:when>
                        <c:otherwise>
                          <td class="hidden-xs text-center"><a
                            href="badData?idApp=${idApp}" data-toggle="tooltip"
                            data-html="true" title="${MaxLengthKey_Matric_3}"
                            data-placement="right"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("MaxLengthKey_Matric_1").toString()))%>
                            </strong>
                          </a></td>
                        </c:otherwise>
                      </c:choose>

                      <td class="hidden-xs">Number of Records Failed</td>
                      <c:choose>
                        <c:when test="${MaxLengthKey_Matric_2 eq null}">

                          <td class="hidden-xs text-center">${MaxLengthKey_Matric_2}</td>
                        </c:when>
                        <c:otherwise>
                          <td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("MaxLengthKey_Matric_2").toString()))%></td>
                        </c:otherwise>
                      </c:choose>

                    </tr>
										<%
											} else if (listapplications.getlengthCheck().equalsIgnoreCase("Y")){
										%>

                    <tr>
                      <!-- <td class="hidden-xs">Length Check (Conformity)</td> -->
                      <td class="hidden-xs"><a onClick="validateHrefVulnerability(this)"
                            href="badData?idApp=${idApp}" data-toggle="tooltip"
                            data-html="true" title="${lengthKey_Matric_3}"
                            data-placement="right">Length Check (Conformity)
                          </a></td>
                      <td class="hidden-xs" style="width: 175px;">
                        <%
                          if (request.getAttribute("lengthCheckScore") != null) {
                          Double lengthCheckScore = Double.valueOf(request.getAttribute("lengthCheckScore").toString());
                          if (lengthCheckScore <= 0) {
                        %>
                        <div class="row" >
                          <div class="col-md-6">
                            <div
                              class="caption font-red-sunglo caption-subject bold blink"
                              style="text-align: center; font-size: 130%">${lengthCheckScore}</div>
                          </div>
                          <div class="col-md-6">
                            &nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
                              id="Length Check"><i class="fa fa-area-chart"
                              style="font-size: 20px"></i></a>
                          </div>
                        </div> <%
  } else {
 %>
                        <div class="row" >
                          <div class="col-md-6">
                            <div id="g18"></div>
                          </div>
                          <div class="col-md-6">
                            <br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
                              id="Length Check"><i class="fa fa-area-chart"
                              style="font-size: 20px"></i></a>
                          </div>
                        </div> <%
  }
 }
 %>
                      </td>
                      <td><c:if test="${lengthStatus eq 'passed'}">
                          <span class="label label-success label-sm">${lengthStatus}</span>
                        </c:if> <c:if test="${lengthStatus eq 'failed'}">

                          <span class="label label-danger label-sm">${lengthStatus}</span>
                        </c:if></td>
                      <%
                        System.out.println("============================Data Completeness");
                      %>


                      <td class="hidden-xs">Number of Columns Tested</td>
                      <c:choose>
                        <c:when test="${lengthKey_Matric_1 eq null}">
                          <td class="hidden-xs text-center">${lengthKey_Matric_1}</td>
                        </c:when>
                        <c:otherwise>
                          <td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
                            href="badData?idApp=${idApp}" data-toggle="tooltip"
                            data-html="true" title="${lengthKey_Matric_3}"
                            data-placement="right"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("lengthKey_Matric_1").toString()))%>
                            </strong>
                          </a></td>
                        </c:otherwise>
                      </c:choose>

                      <td class="hidden-xs">Number of Records Failed</td>
                      <c:choose>
                        <c:when test="${lengthKey_Matric_2 eq null}">

                          <td class="hidden-xs text-center">${lengthKey_Matric_2}</td>
                        </c:when>
                        <c:otherwise>
                          <td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("lengthKey_Matric_2").toString()))%></td>
                        </c:otherwise>
                      </c:choose>

                      <%--  <td class="hidden-xs">Number of Columns Tested</td>
                      <td class="hidden-xs text-center">${lengthKey_Matric_1}</td>
                      <td class="hidden-xs">Number of Records Failed</td>
                      <td class="hidden-xs text-center">${lengthKey_Matric_2}</td> --%>

                    </tr>
                    <%
                      }
                    %>

										<!-- --------End of length Check/ Max Length Check------------ -->

										<%
											if (listapplications.getNonNullCheck().equalsIgnoreCase("Y")) {
										%>
										<tr>
											<!-- <td class="hidden-xs">Data Completeness</td> -->
											<td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
														href="nullstats?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${nullCountKey_Matric_3}"
														data-placement="right">Data Completeness
													</a></td>
											<td class="hidden-xs" style="width: 175px;">
												<%
													if (request.getAttribute("nullCountScore") != null) {
													Double nullCountScore = Double.valueOf(request.getAttribute("nullCountScore").toString());
													if (nullCountScore <= 0) {
												%>
												<div class="row">
													<div class="col-md-6">
														<div
															class="caption font-red-sunglo caption-subject bold blink"
															style="text-align: center; font-size: 130%">${nullCountScore}</div>
													</div>
													<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Null Count"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	} else {
 %>
												<div class="row">
													<div class="col-md-6">
														<div id="g2"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Null Count"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
 }
 %>
											</td>
											<td><c:if test="${nullCountStatus eq 'passed'}">
													<span class="label label-success label-sm">${nullCountStatus}</span>
												</c:if> <c:if test="${nullCountStatus eq 'failed'}">

													<span class="label label-danger label-sm">${nullCountStatus}</span>
												</c:if></td>
											<%
												System.out.println("============================Data Completeness");
											%>


											<td class="hidden-xs">Number of Columns Tested</td>
											<c:choose>
												<c:when test="${nullCountKey_Matric_1 eq null}">
													<td class="hidden-xs text-center">${nullCountKey_Matric_1}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="nullstats?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${nullCountKey_Matric_3}"
														data-placement="right"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("nullCountKey_Matric_1").toString()))%>
														</strong>
													</a></td>
												</c:otherwise>
											</c:choose>

											<td class="hidden-xs">Number of Nulls Identified</td>
											<c:choose>
												<c:when test="${nullCountKey_Matric_2 eq null}">
													<td class="hidden-xs text-center">${nullCountKey_Matric_2}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("nullCountKey_Matric_2").toString()))%></td>
												</c:otherwise>
											</c:choose>

											<%-- <td class="hidden-xs">Number of Columns Tested</td>
											<td class="hidden-xs text-center">${nullCountKey_Matric_1}</td>
											<td class="hidden-xs">Number of Nulls Identified</td>
											<td class="hidden-xs text-center">${nullCountKey_Matric_2}</td> --%>

										</tr>
										<%
											} else {
										}
										%>
										<c:forEach var="listdftranruleDataObj"
											items="${listdftranruleData}">
											<c:if test="${listdftranruleDataObj.value eq 'Y'}">
												<tr>
													<!-- <td class="hidden-xs">Data Uniqueness (Primary Keys)</td> -->
													<td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
																href="dupstats?idApp=${idApp}" data-toggle="tooltip"
																data-html="true" title="${identityfieldsKey_Matric_3}"
																data-placement="right">Data Uniqueness (Primary Keys)
															</a></td>
													<td class="hidden-xs" style="width: 175px;">
														<%
															System.out.println("======== Data Uniqueness");
														if (request.getAttribute("identityFieldsScore") != null) {
															Double identityFieldsScore = Double.valueOf(request.getAttribute("identityFieldsScore").toString());
															if (identityFieldsScore <= 0) {
														%>
														<div class="row">
															<div class="col-md-6">
																<div
																	class="caption font-red-sunglo caption-subject bold blink"
																	style="text-align: center; font-size: 130%">${identityFieldsScore}</div>
															</div>
															<div class="col-md-6">
																&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
																	id="Primary Key Duplicate"><i
																	class="fa fa-area-chart" style="font-size: 20px"></i></a>
															</div>
														</div> <%
 	} else {
 %>
														<div class="row">
															<div class="col-md-6">
																<div id="g4"></div>
															</div>
															<div class="col-md-6">
																<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
																	id="Primary Key Duplicate"><i
																	class="fa fa-area-chart" style="font-size: 20px"></i></a>
															</div>
														</div> <%
 	}
 }
 %>
													</td>
													<td><c:if test="${identityfieldsStatus eq 'passed'}">
															<span class="label label-success label-sm">${identityfieldsStatus}</span>
														</c:if> <c:if test="${identityfieldsStatus eq 'failed'}">
															<span class="label label-danger label-sm">${identityfieldsStatus}</span>
														</c:if></td>

													<td class="hidden-xs">Total Number of Primary Keys</td>
													<c:choose>
														<c:when test="${identityfieldsKey_Matric_1 eq null}">
															<td class="hidden-xs text-center">${identityfieldsKey_Matric_1}</td>
														</c:when>
														<c:otherwise>
															<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
																href="dupstats?idApp=${idApp}" data-toggle="tooltip"
																data-html="true" title="${identityfieldsKey_Matric_3}"
																data-placement="right"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("identityfieldsKey_Matric_1").toString()))%>
																</strong>
															</a></td>
														</c:otherwise>
													</c:choose>

													<td class="hidden-xs">Total Number of Duplicates</td>
													<c:choose>
														<c:when test="${identityfieldsKey_Matric_2 eq null}">
															<td class="hidden-xs text-center">${identityfieldsKey_Matric_2}</td>
														</c:when>
														<c:otherwise>
															<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("identityfieldsKey_Matric_2").toString()))%></td>
														</c:otherwise>
													</c:choose>

													<%-- <td class="hidden-xs">Total Number of Primary Keys </td>
													<td class="hidden-xs text-center">${identityfieldsKey_Matric_1}</td>
													<td class="hidden-xs">Total Number of Duplicates </td>
													<td class="hidden-xs text-center">${identityfieldsKey_Matric_2}</td> --%>
													<!-- <td><a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;"
	id="id_dup_summary"> <i class="fa fa-line-chart">
	Chart</i>
	</a></td> -->
												</tr>
											</c:if>
											<c:if test="${listdftranruleDataObj.key eq 'Y'}">
												<tr>
													<!-- <td class="hidden-xs">Selected Field Uniqueness (User
														Selected Fields)</td> -->
													<td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
																href="dupstats?idApp=${idApp}" data-toggle="tooltip"
																data-html="true" title="${allFieldsKey_Matric_3}"
																data-placement="right">Selected Field Uniqueness (User
														        Selected Fields)
															</a></td>
													<td class="hidden-xs" style="width: 175px;">
														<%
															if (request.getAttribute("allFieldsScore") != null) {

															Double allFieldsScore = Double.valueOf(request.getAttribute("allFieldsScore").toString());
															if (allFieldsScore <= 0) {
														%>
														<div class="row">
															<div class="col-md-6">
																<div
																	class="caption font-red-sunglo caption-subject bold blink"
																	style="text-align: center; font-size: 130%">${allFieldsScore}</div>
															</div>
															<div class="col-md-6">
																&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
																	id="User Selected Fields Duplicate"><i
																	class="fa fa-area-chart" style="font-size: 20px"></i></a>
															</div>
														</div> <%
 	} else {
 %>
														<div class="row">
															<div class="col-md-6">
																<div id="g3"></div>
															</div>
															<div class="col-md-6">
																<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
																	id="User Selected Fields Duplicate"><i
																	class="fa fa-area-chart" style="font-size: 20px"></i></a>
															</div>
														</div> <%
 	}
 }
 %>
													</td>
													<td><c:if test="${allFieldsStatus eq 'passed'}">
															<span class="label label-success label-sm">${allFieldsStatus}</span>
														</c:if> <c:if test="${allFieldsStatus eq 'failed'}">
															<span class="label label-danger label-sm">${allFieldsStatus}</span>
														</c:if></td>
													<td class="hidden-xs">Total Number of Duplicate Keys</td>
													<c:choose>
														<c:when test="${allFieldsKey_Matric_1 eq null}">
															<td class="hidden-xs text-center">${allFieldsKey_Matric_1}</td>
														</c:when>
														<c:otherwise>
															<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
																href="dupstats?idApp=${idApp}" data-toggle="tooltip"
																data-html="true" title="${allFieldsKey_Matric_3}"
																data-placement="right"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("allFieldsKey_Matric_1").toString()))%>
																</strong>
															</a></td>
														</c:otherwise>
													</c:choose>

													<td class="hidden-xs">Total Number of Duplicates</td>
													<c:choose>
														<c:when test="${allFieldsKey_Matric_2 eq null}">
															<td class="hidden-xs text-center">${allFieldsKey_Matric_2}</td>
														</c:when>
														<c:otherwise>
															<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("allFieldsKey_Matric_2").toString()))%></td>
														</c:otherwise>
													</c:choose>
													<%-- <td class="hidden-xs">Total Number of Duplicate Keys</td>
													<td class="hidden-xs text-center">${allFieldsKey_Matric_1}</td>
													<td class="hidden-xs">Total Number of Duplicates</td>
													<td class="hidden-xs text-center">${allFieldsKey_Matric_2}</td> --%>

												</tr>
											</c:if>


										</c:forEach>
										<%
											if (listapplications.getNumericalStatCheck().equalsIgnoreCase("Y")) {
										%>
										<tr>
											<!-- <td class="hidden-xs">Distribution Check</td> -->
											<td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
														href="numericstats?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${numericalFieldKey_Matric_3}"
														data-placement="right">Distribution Check
													</a></td>
											<td class="hidden-xs" style="width: 175px;">
												<%
													if (request.getAttribute("showDQIEmpty") != null) {
													  Object numericalStatsStatus = request.getAttribute("numericalFieldStatsStatus");
													  if (request.getAttribute("numericalFieldScore") != null && numericalStatsStatus !=  null && !numericalStatsStatus.toString().trim().isEmpty()) {
														Double numericalFieldScore = Double.valueOf(request.getAttribute("numericalFieldScore").toString());
														if (numericalFieldScore <= 0) {
												%>
												<div class="row">
													<div class="col-md-6">
														<div
															class="caption font-red-sunglo caption-subject bold blink"
															style="text-align: center; font-size: 130%">${numericalFieldScore}</div>
													</div>
													<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Numerical Field Fingerprint"><i
															class="fa fa-area-chart" style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	} else {
 %>
												<div class="row">
													<div class="col-md-6">
														<div id="g5"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Numerical Field Fingerprint"><i
															class="fa fa-area-chart" style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
 }
 }
 %>
											</td>
											<td><c:if
													test="${numericalFieldStatsStatus eq 'passed'}">
													<span class="label label-success label-sm">${numericalFieldStatsStatus}</span>
												</c:if> <c:if test="${numericalFieldStatsStatus eq 'failed'}">
													<span class="label label-danger label-sm">${numericalFieldStatsStatus}</span>
												</c:if></td>
											<td class="hidden-xs">Number of Columns Tested</td>
											<c:choose>
												<c:when test="${numericalFieldKey_Matric_1 eq null}">
													<td class="hidden-xs text-center">${numericalFieldKey_Matric_1}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="numericstats?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${numericalFieldKey_Matric_3}"
														data-placement="right"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("numericalFieldKey_Matric_1").toString()))%>
														</strong>
													</a></td>
												</c:otherwise>
											</c:choose>

											<% if(listapplications.getKeyGroupRecordCountAnomaly().equalsIgnoreCase("Y")) { %>
											<td class="hidden-xs">Number of Records Failed</td>
											<% } else { %>
											<td class="hidden-xs">Number of Columns Failed</td>
											<% } %>
											<c:choose>
												<c:when test="${numericalFieldKey_Matric_2 eq null}">
													<td class="hidden-xs text-center">${numericalFieldKey_Matric_2}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("numericalFieldKey_Matric_2").toString()))%></td>
												</c:otherwise>
											</c:choose>
											<%-- <td class="hidden-xs">Number of Columns Tested </td>
											<td class="hidden-xs text-center">${numericalFieldKey_Matric_1}</td>
											<td class="hidden-xs">Number of Columns Failed</td>
											<td class="hidden-xs text-center">${numericalFieldKey_Matric_2}
											</td>--%>

										</tr>
										<%
											} else {
										}
										%>
										<%
											if (listapplications.getStringStatCheck().equalsIgnoreCase("Y")) {
										%>
										<tr>
											<td class="hidden-xs">String Data Confidence</td>
											<td class="hidden-xs">
												<%
													if (request.getAttribute("showDQIEmpty") != null) {
													if (request.getAttribute("stringFieldScore") != null) {
														Double stringFieldScore = Double.valueOf(request.getAttribute("stringFieldScore").toString());
														if (stringFieldScore <= 0) {
												%>
												<div class="row">
													<div class="col-md-6">
														<div
															class="caption font-red-sunglo caption-subject bold blink"
															style="text-align: center; font-size: 130%">${stringFieldScore}</div>
													</div>
													<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="String Field Fingerprint"><i
															class="fa fa-area-chart" style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	} else {
 %>
												<div class="row">
													<div class="col-md-6">
														<div id="g6"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="String Field Fingerprint"><i
															class="fa fa-area-chart" style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
 }
 }
 %>
											</td>
											<td><c:if test="${stringFieldStatsStatus eq 'passed'}">
													<span class="label label-success label-sm">${stringFieldStatsStatus}</span>
												</c:if> <c:if test="${stringFieldStatsStatus eq 'failed'}">
													<span class="label label-danger label-sm">${stringFieldStatsStatus}</span>
												</c:if></td>
											<td class="hidden-xs">Number of String Columns</td>
											<c:choose>
												<c:when test="${numberofStringColumnsYes eq null}">
													<td class="hidden-xs text-center">${numberofStringColumnsYes}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("numberofStringColumnsYes").toString()))%></td>
												</c:otherwise>
											</c:choose>

											<td class="hidden-xs">Number of Records Failed</td>
											<c:choose>
												<c:when test="${numberofStringColumnsFailed eq null}">
													<td class="hidden-xs text-center">${numberofStringColumnsFailed}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("numberofStringColumnsFailed").toString()))%></td>
												</c:otherwise>
											</c:choose>
											<%-- <td class="hidden-xs">Number of String Columns</td>
											<td class="hidden-xs text-center">${numberofStringColumnsYes}</td>

											<td class="hidden-xs">Number of Records Failed</td>
											<td class="hidden-xs text-center">${numberofStringColumnsFailed}</td> --%>

										</tr>
										<%
											}
										%>
										<%
											if (listapplications.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {
										%>
										<tr>
											<!-- <td class="hidden-xs">Record Anomaly</td> -->
											<td class="hidden-xs"><a onClick="validateHrefVulnerability(this)"
														href="recordAnomaly?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${recordAnomalyKey_Matric_3}"
														data-placement="right">Record Anomaly
													</a></td>
											<td class="hidden-xs" style="width: 175px;">
												<%
													if (request.getAttribute("recordFieldScore") != null) {
													Double recordFieldScore = Double.valueOf(request.getAttribute("recordFieldScore").toString());
													if (recordFieldScore <= 0) {
												%>
												<div class="row">
													<div class="col-md-6">
														<div
															class="caption font-red-sunglo caption-subject bold blink"
															style="text-align: center; font-size: 130%">${recordFieldScore}</div>
													</div>
													<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Record Anomaly Fingerprint"><i
															class="fa fa-area-chart" style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	} else {
 %>
												<div class="row">
													<div class="col-md-6">
														<div id="g7"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Record Anomaly Fingerprint"><i
															class="fa fa-area-chart" style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
 }
 %>
											</td>
											<td><c:if test="${recordAnomalyStatus eq 'passed'}">
													<span class="label label-success label-sm">${recordAnomalyStatus}</span>
												</c:if> <c:if test="${recordAnomalyStatus eq 'failed'}">
													<span class="label label-danger label-sm">${recordAnomalyStatus}</span>
												</c:if></td>
											<td class="hidden-xs">Number of Columns Tested</td>
											<c:choose>
												<c:when test="${recordAnomalyKey_Matric_1 eq null}">
													<td class="hidden-xs text-center">${recordAnomalyKey_Matric_1}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="recordAnomaly?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${recordAnomalyKey_Matric_3}"
														data-placement="right"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("recordAnomalyKey_Matric_1").toString()))%>
														</strong>
													</a></td>
												</c:otherwise>
											</c:choose>

											<td class="hidden-xs">Number of Records Failed</td>
											<c:choose>
												<c:when test="${recordAnomalyKey_Matric_2 eq null}">
													<td class="hidden-xs text-center">${recordAnomalyKey_Matric_2}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("recordAnomalyKey_Matric_2").toString()))%></td>
												</c:otherwise>
											</c:choose>
											<%-- <td class="hidden-xs">Number of Columns Tested</td>
											<td class="hidden-xs text-center">${recordAnomalyKey_Matric_1}</td>
											<td class="hidden-xs">Number of Records Failed</td>
											<td class="hidden-xs text-center">${recordAnomalyKey_Matric_2}</td> --%>

										</tr>
										<%
											}
										%>

										<%
											if (listapplications.getBadData()!=null && listapplications.getBadData().equalsIgnoreCase("Y")) {
										%>
										<tr>
											<!-- <td class="hidden-xs">Bad Data (Conformity)</td> -->
											<td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
														href="badData?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${badDataKey_Matric_3}"
														data-placement="right">Bad Data (Conformity)
													</a></td>
											<td class="hidden-xs" style="width: 175px;">
												<%
													if (request.getAttribute("badDataScore") != null) {
													Double badDataScore = Double.valueOf(request.getAttribute("badDataScore").toString());
													if (badDataScore <= 0) {
												%>
												<div class="row">
													<div class="col-md-6">
														<div
															class="caption font-red-sunglo caption-subject bold blink"
															style="text-align: center; font-size: 130%">${badDataScore}</div>
													</div>
													<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall" id="Bad Data"><i
															class="fa fa-area-chart" style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	} else {
 %>
												<div class="row">
													<div class="col-md-6">
														<div id="g17"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Bad Data"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
 }
 %>
											</td>
											<td><c:if test="${badDataStatus eq 'passed'}">
													<span class="label label-success label-sm">${badDataStatus}</span>
												</c:if> <c:if test="${badDataStatus eq 'failed'}">
													<span class="label label-danger label-sm">${badDataStatus}</span>
												</c:if></td>
											<td class="hidden-xs">Number of Columns Tested</td>
											<c:choose>
												<c:when test="${badDataKey_Matric_1 eq null}">
													<td class="hidden-xs text-center">${badDataKey_Matric_1}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="badData?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${badDataKey_Matric_3}"
														data-placement="right"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("badDataKey_Matric_1").toString()))%>
														</strong>
													</a></td>
												</c:otherwise>
											</c:choose>

											<td class="hidden-xs">Number of Bad Data</td>
											<c:choose>
												<c:when test="${badDataKey_Matric_2 eq null}">
													<td class="hidden-xs text-center">${badDataKey_Matric_2}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("badDataKey_Matric_2").toString()))%></td>
												</c:otherwise>
											</c:choose>
											<%-- <td class="hidden-xs">Number of Columns Tested</td>
											<td class="hidden-xs text-center">${badDataKey_Matric_1}</td>
											<td class="hidden-xs">Number of Bad Data</td>
											<td class="hidden-xs text-center">${badDataKey_Matric_2}</td> --%>

										</tr>
										<%
											}
										%>

										<!-- for pattern match-unmatch	by pravin	 -->
										<%
											if (listapplications.getPatternCheck()!=null && listapplications.getPatternCheck().equalsIgnoreCase("Y")) {
										%>
										<tr>
											<!-- <td class="hidden-xs">Pattern UnMatch Data (Conformity)</td> -->
											<td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
												href="badData?idApp=${idApp}" data-toggle="tooltip"
												data-html="true" title="${patternDataKey_Matric_3}"
												data-placement="right"> Regex Pattern Check (Conformity)
												<!--data-placement="right">Pattern UnMatch Data (Conformity)-->
											</a></td>
											<td class="hidden-xs" style="width: 175px;">
												<%
													if (request.getAttribute("patternDataScore") != null) {
													Double patternDataScore = Double.valueOf(request.getAttribute("patternDataScore").toString());

													if (patternDataScore <= 0) {
												%>
												<div class="row">
													<div class="col-md-6">
														<div
															class="caption font-red-sunglo caption-subject bold blink"
															style="text-align: center; font-size: 130%">${patternDataScore}</div>
													</div>
													<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Regex Pattern Check"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	} else {
 %>
												<div class="row">
													<div class="col-md-6">
														<div id="g20"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Regex Pattern Check"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
 }
 %>
											</td>
											<td><c:if test="${patternDataStatus eq 'passed'}">
													<span class="label label-success label-sm">${patternDataStatus}</span>
												</c:if> <c:if test="${patternDataStatus eq 'failed'}">
													<span class="label label-danger label-sm">${patternDataStatus}</span>
												</c:if></td>
											<%--	<td class="hidden-xs">Number of Columns Tested</td>
											<c:choose>
												<c:when test="${patternDataKey_Matric_1 eq null}">
													<td class="hidden-xs text-center">${patternDataKey_Matric_1}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double
										.parseDouble(request.getParameter("patternDataKey_Matric_1").toString()))%></td>
												</c:otherwise>
											</c:choose>

											<td class="hidden-xs">Number of Records Failed</td>
											<c:choose>
												<c:when test="${patternDataKey_Matric_2 eq null}">
													<td class="hidden-xs text-center">${patternDataKey_Matric_2}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double
										.parseDouble(request.getParameter("patternDataKey_Matric_2").toString()))%></td>
												</c:otherwise>
											</c:choose> --%>
											<td class="hidden-xs">Number of Columns Tested</td>
											<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
												href="badData?idApp=${idApp}" data-toggle="tooltip"
												data-html="true" title="${patternDataKey_Matric_3}"
												data-placement="right"> <strong>
														${patternDataKey_Matric_1} </strong>
											</a></td>
											<td class="hidden-xs">Number of Records Failed</td>
											<td class="hidden-xs text-center">${patternDataKey_Matric_2}</td>

										</tr>
										<%
											}
										%>
	<!-- for default pattern match-unmatch	 -->
										<%
											if (listapplications.getDefaultPatternCheck()!=null && listapplications.getDefaultPatternCheck().equalsIgnoreCase("Y")) {
												%>
												<tr>
													<!-- <td class="hidden-xs">Pattern UnMatch Data (Conformity)</td> -->
													<td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
														href="badData?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${defaultPatternDataKey_Matric_3}"
														data-placement="right">Default Pattern Check (Conformity)
													</a></td>
													<td class="hidden-xs">
														<%
															if (request.getAttribute("defaultPatternDataScore") != null) {
															Double defaultPatternDataScore = Double.valueOf(request.getAttribute("defaultPatternDataScore").toString());
															if (defaultPatternDataScore <= 0) {
														%>
														<div class="row">
															<div class="col-md-6">
																<div
																	class="caption font-red-sunglo caption-subject bold blink"
																	style="text-align: center; font-size: 130%">${defaultPatternDataScore}</div>
															</div>
															<div class="col-md-6">
																&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
																	id="Default Pattern Check"><i class="fa fa-area-chart"
																	style="font-size: 20px"></i></a>
															</div>
														</div> <%
		 	} else {
		 %>
														<div class="row">
															<div class="col-md-6">
																<div id="g25"></div>
															</div>
															<div class="col-md-6">
																<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
																	id="Default Pattern Check"><i class="fa fa-area-chart"
																	style="font-size: 20px"></i></a>
															</div>
														</div> <%
		 	}
		 }
		 %>
													</td>
													<td><c:if test="${defaultPatternDataStatus eq 'passed'}">
															<span class="label label-success label-sm">${defaultPatternDataStatus}</span>
														</c:if> <c:if test="${defaultPatternDataStatus eq 'failed'}">
															<span class="label label-danger label-sm">${defaultPatternDataStatus}</span>
														</c:if></td>
													<%--	<td class="hidden-xs">Number of Columns Tested</td>
													<c:choose>
														<c:when test="${patternDataKey_Matric_1 eq null}">
															<td class="hidden-xs text-center">${patternDataKey_Matric_1}</td>
														</c:when>
														<c:otherwise>
															<td class="hidden-xs text-center"><%=df.format(Double
												.parseDouble(request.getParameter("patternDataKey_Matric_1").toString()))%></td>
														</c:otherwise>
													</c:choose>

													<td class="hidden-xs">Number of Records Failed</td>
													<c:choose>
														<c:when test="${patternDataKey_Matric_2 eq null}">
															<td class="hidden-xs text-center">${patternDataKey_Matric_2}</td>
														</c:when>
														<c:otherwise>
															<td class="hidden-xs text-center"><%=df.format(Double
												.parseDouble(request.getParameter("patternDataKey_Matric_2").toString()))%></td>
														</c:otherwise>
													</c:choose> --%>
													<td class="hidden-xs">Number of Columns Tested</td>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="badData?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${defaultPatternDataKey_Matric_3}"
														data-placement="right"> <strong>
																${defaultPatternDataKey_Matric_1} </strong>
													</a></td>
													<td class="hidden-xs">Number of Records Failed</td>
													<td class="hidden-xs text-center">${defaultPatternDataKey_Matric_2}</td>

												</tr>
												<%
													}
												%>




										<%
											if ((listapplications.getDateRuleChk()!=null && listapplications.getDateRuleChk().equalsIgnoreCase("Y"))
												|| (listapplications.getdGroupDateRuleCheck()!=null && listapplications.getdGroupDateRuleCheck().equalsIgnoreCase("Y"))) {
										%>
										<tr>
											<!-- <td class="hidden-xs">Date Rule Check (Record Anomaly)</td> -->
											<td class="hidden-xs"><a onClick="validateHrefVulnerability(this)"
														href="recordAnomaly?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${dateRuleKey_Matric_3}"
														data-placement="right">Date Rule Check (Record Anomaly)
													</a></td>
											<td class="hidden-xs">
												<%
													if (request.getAttribute("dateRuleCheckScore") != null) {
													Double dateRuleCheckScore = Double.valueOf(request.getAttribute("dateRuleCheckScore").toString());
													if (dateRuleCheckScore <= 0) {
												%>
												<div class="row">
													<div class="col-md-6">
														<div
															class="caption font-red-sunglo caption-subject bold blink"
															style="text-align: center; font-size: 130%">${dateRuleCheckScore}</div>
													</div>
													<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Date Rule Check"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	} else {
 %>
												<div class="row">
													<div class="col-md-6">
														<div id="g16"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Date Rule Check"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
 }
 %>
											</td>
											<td><c:if test="${dateRuleChkStatus eq 'passed'}">
													<span class="label label-success label-sm">${dateRuleChkStatus}</span>
												</c:if> <c:if test="${dateRuleChkStatus eq 'failed'}">

													<span class="label label-danger label-sm">${dateRuleChkStatus}</span>
												</c:if></td>

											<td class="hidden-xs">Number of Columns Tested</td>
											<c:choose>
												<c:when test="${dateRuleKey_Matric_1 eq null}">
													<td class="hidden-xs text-center">${dateRuleKey_Matric_1}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="recordAnomaly?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${dateRuleKey_Matric_3}"
														data-placement="right"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("dateRuleKey_Matric_1").toString()))%>
														</strong>
													</a></td>
												</c:otherwise>
											</c:choose>

											<td class="hidden-xs">Number of Records Failed</td>
											<c:choose>
												<c:when test="${dateRuleKey_Matric_2 eq null}">
													<td class="hidden-xs text-center">${dateRuleKey_Matric_2}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("dateRuleKey_Matric_2").toString()))%></td>
												</c:otherwise>
											</c:choose>
											<%-- <td class="hidden-xs">Number of Columns Tested</td>
											<td class="hidden-xs text-center">${dateRuleKey_Matric_1}</td>
											<td class="hidden-xs">Number of Records Failed</td>
											<td class="hidden-xs text-center">${dateRuleKey_Matric_2}</td> --%>

										</tr>
										<%
											}
										%>

										<%
											if (listapplications.getTimelinessKeyChk()!=null && listapplications.getTimelinessKeyChk().equalsIgnoreCase("Y")) {
										%>
										<tr>
											<!-- <td class="hidden-xs">Timeliness</td> -->
											<td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
														href="timelinessCheck?idApp=${idApp}"
														data-toggle="tooltip" data-html="true"
														title="${TimelinessCheckKey_Matric_3}"
														data-placement="right">Timeliness
													</a></td>
											<td class="hidden-xs">
												<%
													if (request.getAttribute("TimelinessCheckScore") != null) {
													Double TimelinessCheckScore = Double.valueOf(request.getAttribute("TimelinessCheckScore").toString());
													if (TimelinessCheckScore <= 0) {
												%>
												<div class="row">
													<div class="col-md-6">
														<div
															class="caption font-red-sunglo caption-subject bold blink"
															style="text-align: center; font-size: 130%">${TimelinessCheckScore}</div>
													</div>
													<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Timeliness Fingerprint"><i
															class="fa fa-area-chart" style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	} else {
 %>
												<div class="row">
													<div class="col-md-6">
														<div id="g19"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Timeliness Fingerprint"><i
															class="fa fa-area-chart" style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
 %>
											</td>
											<td><c:if test="${TimelinessCheckStatus eq 'passed'}">
													<span class="label label-success label-sm">${TimelinessCheckStatus}</span>
												</c:if> <c:if test="${TimelinessCheckStatus eq 'failed'}">
													<span class="label label-danger label-sm">${TimelinessCheckStatus}</span>
												</c:if></td>
											<td class="hidden-xs">Number of Segments Tested for
												Continuity</td>
											<c:choose>
												<c:when test="${TimelinessCheckKey_Matric_1 eq null}">
													<td class="hidden-xs text-center">${TimelinessCheckKey_Matric_1}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="timelinessCheck?idApp=${idApp}"
														data-toggle="tooltip" data-html="true"
														title="${TimelinessCheckKey_Matric_3}"
														data-placement="right"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("TimelinessCheckKey_Matric_1").toString()))%>
														</strong>
													</a></td>
												</c:otherwise>
											</c:choose>

											<td class="hidden-xs">Number of Records Failed Contuity
												Test</td>
											<c:choose>
												<c:when test="${TimelinessCheckKey_Matric_2 eq null}">
													<td class="hidden-xs text-center">${TimelinessCheckKey_Matric_2}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("TimelinessCheckKey_Matric_2").toString()))%></td>
												</c:otherwise>
											</c:choose>
											<%-- <td class="hidden-xs">Number of Segments Tested for Continuity</td>
											<td class="hidden-xs text-center">${TimelinessCheckKey_Matric_1}</td>
											<td class="hidden-xs">Number of Records Failed Contuity Test</td>
											<td class="hidden-xs text-center">${TimelinessCheckKey_Matric_2}</td> --%>

										</tr>
										<%
											}
										}
										%>

										<%
											Integer custrule_count = (Integer) request.getAttribute("custrule_count");
										Integer sqlrule_count = (Integer) request.getAttribute("sqlrule_count");

										Integer globalrule_count = (Integer) request.getAttribute("globalrule_count");

										if (custrule_count >= 1) {

											if (listapplications.getApplyRules().equalsIgnoreCase("Y")) {
										%>
										<tr>
											<!-- <td class="hidden-xs">Custom Rules (Referential, Orphan,
												Cross Referential and Regex Rules)</td> -->
											<td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
												href="sqlRules?idApp=${idApp}">Custom Rules (Referential, Orphan,
												Cross Referential and Regex Rules)
											</a></td>
											<td class="hidden-xs" style="width: 175px;">
												<%
													if (request.getAttribute("ruleScoreDF") != null) {
													Double ruleScoreDF = Double.valueOf(request.getAttribute("ruleScoreDF").toString());
													if (ruleScoreDF <= 0) {
												%>
												<div class="row" >
												<div class="col-md-6">
												<div
													class="caption font-red-sunglo caption-subject bold blink"
													style="text-align: center; font-size: 130%">${ruleScoreDF}</div>
												</div>
													<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Rules"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
													} else {
												%>
												<div class="row">
													<div class="col-md-6">
														<div id="g8"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Rules"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
 }
 %>
											</td>
											<td><c:if test="${ruleStatus eq 'passed'}">
													<span class="label label-success label-sm">${ruleStatus}</span>
												</c:if> <c:if test="${ruleStatus eq 'failed'}">
													<span class="label label-danger label-sm">${ruleStatus}</span>
												</c:if></td>
											<td>Total No. of Rules Processed</td>
											<c:choose>
												<c:when test="${ ruleKey_Matric_1 eq null || empty ruleKey_Matric_1}">
													<td class="hidden-xs text-center">${ruleKey_Matric_1}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="sqlRules?idApp=${idApp}"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("ruleKey_Matric_1").toString()))%>
														</strong>
													</a></td>
												</c:otherwise>
											</c:choose>
											<td>Total No. of Rules Failed</td>
											<c:choose>
												<c:when test="${ruleKey_Matric_2 eq null || empty ruleKey_Matric_2}">
													<td class="hidden-xs text-center">${ruleKey_Matric_2}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("ruleKey_Matric_2").toString()))%></td>
												</c:otherwise>
											</c:choose>
										</tr>
										<%
											}
										}
										if (globalrule_count >= 1) {
										%>
										<!-- Global rules 07-06-2019 pravin  -->
										<%
											if (listapplications.getApplyRules().equalsIgnoreCase("Y")) {
										%>
										<tr>
											<!-- <td class="hidden-xs">Global Rules</td> -->
											<td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
												href="sqlRules?idApp=${idApp}">Global Rules
											</a></td>
											<td>
												<%
													if (request.getAttribute("GlobalruleScoreDF") != null) {
													Double ruleScoreDF = Double.valueOf(request.getAttribute("GlobalruleScoreDF").toString());
													if (ruleScoreDF <= 0) {
												%>
												<div
													class="caption font-red-sunglo caption-subject bold blink"
													style="text-align: center; font-size: 130%">${GlobalruleScoreDF}</div>
												<%
													} else {
												%>
												<div class="row">
													<div class="col-md-6">
														<div id="g21"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Global Rules"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
 }
 %>
											</td>
											<td><c:if test="${GlobalruleStatus eq 'passed'}">
													<span class="label label-success label-sm">${GlobalruleStatus}</span>
												</c:if> <c:if test="${GlobalruleStatus eq 'failed'}">
													<span class="label label-danger label-sm">${GlobalruleStatus}</span>
												</c:if></td>
											<td>Total No. of Rules Processed</td>
											<c:choose>
												<c:when test="${ ruleKey_Matric_3 eq null || empty ruleKey_Matric_3}">
													<td class="hidden-xs text-center">${ruleKey_Matric_3}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="sqlRules?idApp=${idApp}"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("ruleKey_Matric_3").toString()))%>
														</strong>
													</a></td>
												</c:otherwise>
											</c:choose>
											<td>Total No. of Rules Failed</td>
											<c:choose>
												<c:when test="${ruleKey_Matric_4 eq null || empty ruleKey_Matric_4}">
													<td class="hidden-xs text-center">${ruleKey_Matric_4}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("ruleKey_Matric_4").toString()))%></td>
												</c:otherwise>
											</c:choose>
											<%-- 	<td>Total No. of Records Processed</td>
												<td class="hidden-xs text-center">${ruleKey_Matric_3}</td>
												<td>Total No. of Records Failed</td>
												<td class="hidden-xs text-center">${ruleKey_Matric_4}</td> --%>
										</tr>

										<%
											}
										}
										if (sqlrule_count >= 1) {
										%>

										<%
											//added for SQL-Rules

										if (listapplications.getApplyRules().equalsIgnoreCase("Y")) {
										%>
										<tr>
											<!-- <td class="hidden-xs">SQL Rules (Custom Rules)</td> -->
											<td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
												href="sqlRules?idApp=${idApp}">SQL Rules (Custom Rules)
											</a></td>
											<td>
												<%
													if (request.getAttribute("sqlRuleScoreDF") != null) {
													Double ruleScoreDF = Double.valueOf(request.getAttribute("sqlRuleScoreDF").toString());
													System.out.println("@@@@@@@@@@@@@@@@@@@ ruleScoreDF ==>" + ruleScoreDF);
													if (ruleScoreDF <= 0) {
												%>
												<div class="row" >
												<div class="col-md-6">
												<div
													class="caption font-red-sunglo caption-subject bold blink"
													style="text-align: center; font-size: 130%">${sqlRuleScoreDF}</div>
												</div>
												<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id=SqlRules><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
													} else {
												%>
												<div class="row">
													<div class="col-md-6">
														<div id="g22"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="SqlRules"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
 }
 %>
											</td>
											<td><c:if test="${sqlRuleStatus eq 'passed'}">
													<span class="label label-success label-sm">${sqlRuleStatus}</span>
												</c:if> <c:if test="${sqlRuleStatus eq 'failed'}">
													<span class="label label-danger label-sm">${sqlRuleStatus}</span>
												</c:if></td>
											<td>Total No. of Records Processed</td>
											<c:choose>
												<c:when test="${ ruleKey_Matric_5 eq null || empty ruleKey_Matric_5}">
													<td class="hidden-xs text-center">${ruleKey_Matric_5}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="sqlRules?idApp=${idApp}"><strong><%=df.format(Double.parseDouble(request.getAttribute("ruleKey_Matric_5").toString()))%></strong></a></td>
												</c:otherwise>
											</c:choose>
											<td>Total No. of Records Failed</td>
											<c:choose>
												<c:when test="${ruleKey_Matric_6 eq null || empty ruleKey_Matric_6}">
													<td class="hidden-xs text-center">${ruleKey_Matric_6}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("ruleKey_Matric_6").toString()))%></td>
												</c:otherwise>
											</c:choose>
										</tr>
										<%
											}
										}
										if (listapplications.getDataDriftCheck().equalsIgnoreCase("Y")
												|| listapplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
										%>
										<tr>
											<!-- <td class="hidden-xs">String Value Drift (Drift and
												Orphan)</td> -->
								            <td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
														href="stringstats?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${dataDriftKey_Matric_3}"
														data-placement="right">String Value Drift (Drift and
												     Orphan)
													</a></td>
											<td>
												<%
												    Object dataDriftStatus = request.getAttribute("dataDriftStatus");
													if (request.getAttribute("dataDriftScore") != null && dataDriftStatus !=null && !dataDriftStatus.toString().trim().isEmpty()) {
													Double ruleScoreDF = Double.valueOf(request.getAttribute("dataDriftScore").toString());
													if (ruleScoreDF <= 0) {
												%>
												<div class="row">
													<div class="col-md-6">
														<div
															class="caption font-red-sunglo caption-subject bold blink"
															style="text-align: center; font-size: 130%">${dataDriftScore}</div>
													</div>
													<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Data Drift"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	} else {
 %>
												<div class="row">
													<div class="col-md-6">
														<div id="g9"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Data Drift"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
  }
 %>
											</td>
											<td><c:if test="${dataDriftStatus eq 'passed'}">
													<span class="label label-success label-sm">${dataDriftStatus}</span>
												</c:if> <c:if test="${dataDriftStatus eq 'failed'}">
													<span class="label label-danger label-sm">${dataDriftStatus}</span>
												</c:if></td>

											<td>Number of Columns Tested</td>
											<c:choose>
												<c:when test="${dataDriftKey_Matric_1 eq null}">
													<td class="hidden-xs text-center">${dataDriftKey_Matric_1}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="stringstats?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${dataDriftKey_Matric_3}"
														data-placement="right"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("dataDriftKey_Matric_1").toString()))%>
														</strong>
													</a></td>
												</c:otherwise>
											</c:choose>

											<td>Number of Unique Value Changed</td>
											<c:choose>
												<c:when test="${dataDriftKey_Matric_2 eq null}">
													<td class="hidden-xs text-center">${dataDriftKey_Matric_2}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("dataDriftKey_Matric_2").toString()))%></td>
												</c:otherwise>
											</c:choose>

											<%-- <td>Number of Columns Tested </td>
												<td class="hidden-xs text-center">${dataDriftKey_Matric_1}</td>
												<td>Number of Unique Value Changed</td>
												<td class="hidden-xs text-center">${dataDriftKey_Matric_2}</td> --%>
										</tr>
										<%
											}
										%>
										<c:if test="${groupEqualityStatus ne null }">
											<tr>
												<td class="hidden-xs">Group Equality</td>
												<td></td>
												<td><c:if test="${groupEqualityStatus eq 'passed'}">
														<span class="label label-success label-sm">${groupEqualityStatus}</span>
													</c:if> <c:if test="${groupEqualityStatus eq 'failed'}">
														<span class="label label-danger label-sm">${groupEqualityStatus}</span>
													</c:if></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
											</tr>
										</c:if>

										<%
											if (listapplications.getDefaultCheck().equalsIgnoreCase("Y")) {
										%>
										<tr>
											<!-- <td class="hidden-xs">Default Check</td> -->
											<td class="hidden-xs "><a onClick="validateHrefVulnerability(this)"
														href="nullstats?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${DefaultCheckKey_Matric_3}"
														data-placement="right">Default Check
													</a></td>
											<td class="hidden-xs">
												<%
													if (request.getAttribute("DefaultCheckScore") != null) {
													Double DefaultCheckScore = Double.valueOf(request.getAttribute("DefaultCheckScore").toString());
													if (DefaultCheckScore <= 0) {
												%>
												<div class="row">
													<div class="col-md-6">
														<div
															class="caption font-red-sunglo caption-subject bold blink"
															style="text-align: center; font-size: 130%">${DefaultCheckScore}</div>
													</div>
													<div class="col-md-6">
														&nbsp&nbsp&nbsp&nbsp&nbsp<a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Default Check"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	} else {
 %>
												<div class="row">
													<div class="col-md-6">
														<div id="g23"></div>
													</div>
													<div class="col-md-6">
														<br /> <br />&nbsp&nbsp&nbsp&nbsp <a onClick="validateHrefVulnerability(this)"  class="hrefCall"
															id="Default Check"><i class="fa fa-area-chart"
															style="font-size: 20px"></i></a>
													</div>
												</div> <%
 	}
 %>
											</td>
											<td><c:if test="${DefaultCheckStatus eq 'passed'}">
													<span class="label label-success label-sm">${DefaultCheckStatus}</span>
												</c:if> <c:if test="${DefaultCheckStatus eq 'failed'}">
													<span class="label label-danger label-sm">${DefaultCheckStatus}</span>
												</c:if></td>
											<td class="hidden-xs">Number of Columns Tested</td>
											<c:choose>
												<c:when test="${DefaultCheckKey_Matric_1 eq null}">
													<td class="hidden-xs text-center">${DefaultCheckKey_Matric_1}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><a onClick="validateHrefVulnerability(this)"
														href="nullstats?idApp=${idApp}" data-toggle="tooltip"
														data-html="true" title="${DefaultCheckKey_Matric_3}"
														data-placement="right"> <strong> <%=df.format(Double.parseDouble(request.getAttribute("DefaultCheckKey_Matric_1").toString()))%>
														</strong>
													</a></td>
												</c:otherwise>
											</c:choose>

											<td class="hidden-xs">Number of Columns Failed</td>
											<c:choose>
												<c:when test="${DefaultCheckKey_Matric_2 eq null}">
													<td class="hidden-xs text-center">${DefaultCheckKey_Matric_2}</td>
												</c:when>
												<c:otherwise>
													<td class="hidden-xs text-center"><%=df.format(Double.parseDouble(request.getAttribute("DefaultCheckKey_Matric_2").toString()))%></td>
												</c:otherwise>
											</c:choose>
											<%-- <td class="hidden-xs">Number of Segments Tested for Continuity</td>
											<td class="hidden-xs text-center">${TimelinessCheckKey_Matric_1}</td>
											<td class="hidden-xs">Number of Records Failed Contuity Test</td>
											<td class="hidden-xs text-center">${TimelinessCheckKey_Matric_2}</td> --%>

										</tr>
										<%
											}
										}
										%>


										<c:forEach items="${fileNameandcolumnOrderStatus}"
											var="fileNameandcolumnOrderStatusobj">
											<tr>
												<td class="hidden-xs">File Content Validation</td>
												<td class="hidden-xs"></td>
												<td><c:if
														test="${fileNameandcolumnOrderStatusobj.fileNameValidationStatus eq 'passed'}">
														<span class="label label-success label-sm">${fileNameandcolumnOrderStatusobj.fileNameValidationStatus}</span>
													</c:if> <c:if
														test="${fileNameandcolumnOrderStatusobj.fileNameValidationStatus eq 'failed'}">
														<span class="label label-danger label-sm">${fileNameandcolumnOrderStatusobj.fileNameValidationStatus}</span>
													</c:if></td>
												<td class="hidden-xs"></td>
												<td class="hidden-xs"></td>
												<td class="hidden-xs"></td>
												<td class="hidden-xs"></td>

												<!-- <td class="hidden-xs"></td> -->
											</tr>
											<tr>
												<td class="hidden-xs">Column Order Validation</td>
												<td class="hidden-xs"></td>
												<td><c:if
														test="${fileNameandcolumnOrderStatusobj.columnOrderValidationStatus eq 'passed'}">
														<span class="label label-success label-sm">${fileNameandcolumnOrderStatusobj.columnOrderValidationStatus}</span>
													</c:if> <c:if
														test="${fileNameandcolumnOrderStatusobj.columnOrderValidationStatus eq 'failed'}">
														<span class="label label-danger label-sm">${fileNameandcolumnOrderStatusobj.columnOrderValidationStatus}</span>
													</c:if></td>
												<td class="hidden-xs"></td>
												<td class="hidden-xs"></td>
												<td class="hidden-xs"></td>
												<td class="hidden-xs"></td>

												<!-- <td class="hidden-xs"></td> -->
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
							<c:if test="${incrementalMatching eq 'Y'}">
								<div class="portlet-body">
									<div class="caption font-red-sunglo">
										<span class="caption-subject bold ">Missing Dates <a onClick="validateHrefVulnerability(this);javascript:downloadCsvReports('${idApp}','MissingDates','MissingDates')"

											href="#" class="CSVStatus" id="">(Download CSV)</a></span>
									</div>
								</div>
							</c:if>




							<%
								if (true) {
							%>
							<!--Div that will hold the chart-->

							<%
								if (fileNameandcolumnOrderStatusDummy.size() != 0) {
							%>

							<!-- <div id="record_summary_div"
								style="height: 300px; width: 100%; margin: 0 auto;"
								class="dashboard-summary-chart"></div> -->
							<%
								}
							%>
							<!--Div that will hold the chart-->
							<div id="null_summary_div"
								style="height: 300px; width: 100%; margin: 0 auto; display: none;"
								class="dashboard-summary-chart"></div>

							<!--Div that will hold the chart-->
							<div id="all_dup_summary_div"
								style="height: 300px; width: 100%; margin: 0 auto; display: none;"
								class="dashboard-summary-chart"></div>

							<!--Div that will hold the chart-->
							<div id="id_dup_summary_div"
								style="height: 300px; width: 100%; margin: 0 auto; display: none;"
								class="dashboard-summary-chart"></div>

							<!--Div that will hold the chart-->
							<div id="num_summary_div"
								style="height: 300px; width: 100%; margin: 0 auto; display: none;"
								class="dashboard-summary-chart"></div>

							<!--Div that will hold the chart-->
							<div id="str_summary_div"
								style="height: 300px; width: 100%; margin: 0 auto; display: none;"
								class="dashboard-summary-chart"></div>
							<!--Div that will hold the chart-->
							<div id="recordanomaly_summary_div"
								style="height: 300px; width: 100%; margin: 0 auto; display: none;"
								class="dashboard-summary-chart"></div>
							<!--Div that will hold the chart-->
							<div id="Length_Check_div"
								style="height: 300px; width: 100%; margin: 0 auto; display: none;"
								class="dashboard-summary-chart"></div>
							<!-- <div id="Bad_Data_div"
								style="height: 300px; width: 100%; margin: 0 auto; display: none;"
								class="dashboard-summary-chart"></div>
 -->
							<div class="clearfix"></div>

							<%-- <b>Filter Date: </b> <input type="text" id="toDate"
								class="md-check" Value="${toDate}"> <input type="text"
								id="fromDate" class="md-check" value="${fromDate}"> <input
								type="button" id="filterDate" class="btn btn-denger"
								value="Search">

								<b>Select Run</b>
								<input type = "text" id = "RunFilter"  value="${Run}" />
								<input type = "button" value = "submit" id="runFilterBtn" class="btn btn-denger" /> --%>



							<%
								}
							}
							} catch (Exception e) {
								e.printStackTrace();
							}
							%><br /> <br />



							<!--formden.js communicates with FormDen server to validate fields and submit via AJAX -->
							<script type="text/javascript"
								src="./assets/global/plugins/formden.js"></script>

							<!-- Special version of Bootstrap that is isolated to content wrapped in .bootstrap-iso -->
							<link rel="stylesheet"
								href="./assets/global/plugins/bootstrap-iso.css" />

							<!--Font Awesome (added because you use icons in your prepend/append)-->
							<link rel="stylesheet"
								href="./assets/global/plugins/font-awesome.min.css" />

							<!-- Inline CSS based on choices in "Settings" tab -->
							<style>
.bootstrap-iso .formden_header h2, .bootstrap-iso .formden_header p,
	.bootstrap-iso form {
	font-family: Arial, Helvetica, sans-serif;
	color: black
}

.bootstrap-iso form button, .bootstrap-iso form button:hover {
	color: white !important;
}

.asteriskField {
	color: red;
}
</style>

							<div class="portlet-body">
								<div class="col-sm-9">
									<div class="row">
										<div class="" style="margin: -18px 48px">
											<b>From Date</b>
										</div>
										<div class="" style="margin: 0px 250px">
											<b>To Date</b>
										</div>
									</div>
								</div>
								<div class="col-sm-3">
									<b>Run Filter</b>
								</div>
							</div>

							<!-- HTML Form (wrapped in a .bootstrap-iso div) -->
							<div class="Row">
								<div class="col-sm-9">
									<div class="bootstrap-iso">
										<div class="container-fluid">
											<div class="row">
												<div class="col-md-9 col-sm-9 col-xs-12">
													<form id="tokenForm1" action=""
														class="form-horizontal" method="post">
														<div class="form-group ">

															<div class="col-sm-10">
																<div class="input-group">

																	<div class="Row">
																		<div class="col-sm-5">
																			<input onchange="ManageDateRange.storeChangedDates(this, 1)" data-fromdate=Y class="toDate" id="date" name="date"
																				placeholder="From Date" type="text"
																				value="${toDate}" />
																		</div>
																		<div class="col-sm-5">
																			<input onchange="ManageDateRange.storeChangedDates(this, 2)" data-todate=Y class="fromDate" id="date" name="date"
																				placeholder="To date" type="text"
																				value="${fromDate}" />
																		</div>
																		<div class="col-sm-2">
																			<input type="button" id="filterDate"
																				class="btn btn-denger" value="Search"
																				style="font-size : 12px; width: 80px; height: 25px;
																				 text-align: center;  font-weight: bold;">
																		</div>
																	</div>

																</div>
															</div>
														</div>
													</form>
												</div>
												<div id="updatethresholdWaitModel" class="modal" role="dialog" style="top: 200px;">
                                                    <div class="modal-dialog" style="width:400px">
                                                        <!-- Modal content-->
                                                        <div class="modal-content">
                                                            <div class="modal-body blueClass">
                                                                <img alt="" src="./assets/img/reload.gif" width='32px' height='30px'
                                                                style="padding-right:10px;" /><b>Please wait while updating the threshold...</b>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
											</div>
										</div>
									</div>
								</div>

								 <!--<div class="col-sm-3" style="margin: -94px -60px">Mamta 21/3/2022 to place search buttoninside div -->
                                 <div class="col-sm-3" style="margin: -94px -90px">
										<input type="text" id="RunFilter" value="${Run}"
										style="margin: 96px 58px;" /> <input type="button"
										class="btn btn-denger" value="Search" id="runFilterBtn"
										style="margin: -125px 240px;  font-size : 12px;  font-weight: bold; height: 28px;
										  vertical-align: middle; text-align: center;" />
								</div>
							</div>
							<br />
                                <span style="display:inline-block; height: 50px;"></span>
							<script type="text/javascript"
								src="./assets/global/plugins/loader.js"></script>
							<script type="text/javascript">
      google.charts.load('current', {'packages':['gauge']});
      google.charts.load('current', {'packages':['corechart']});

      google.charts.setOnLoadCallback(drawChart);



      function drawChart() {

        var data = google.visualization.arrayToDataTable([
          ['Label', 'Value'],
          ['DQI', ${totalDQI}]
        ]);

        var options = {
          width: 400, height: 120,
          redFrom: 0, redTo: 50,
          yellowFrom:50, yellowTo: 80,
          greenFrom: 80 , greenTo:100,
          minorTicks: 5
        };

        var chart = new google.visualization.Gauge(document.getElementById('AggDqiChart_div'));

        chart.draw(data, options);
      }

</script>

							<!-- Extra JavaScript/CSS added manually in "Settings" tab -->
							<!-- Include jQuery -->
							<script type="text/javascript"
								src="./assets/global/plugins/jquery-1.11.3.min.js"></script>

							<!-- Include Date Range Picker -->
							<script type="text/javascript"
								src="./assets/global/plugins/bootstrap-datepicker.min.js"></script>
							<link rel="stylesheet"
								href="./assets/global/plugins/bootstrap-datepicker3.css" />
							<script src="https://code.jquery.com/jquery-3.2.1.min.js"
							integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
							crossorigin="anonymous"></script>
                            <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"
                            integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
                            crossorigin="anonymous"></script>
                            <link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
							<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>

    <script>
            $(document).ready(function() {
                $('#tokenForm1').ajaxForm({
                    headers: {'token':$("#token").val()}
                });
                initializeJwfSpaInfra();
            });
    </script>

	<script>
	$(document).ready(function(){
		var date_input=$('input[name="date"]'); //our date input has the name "date"
		var container=$('.bootstrap-iso form').length>0 ? $('.bootstrap-iso form').parent() : "body";
		date_input.datepicker({
			format: 'yyyy-mm-dd',
			container: container,
			todayHighlight: true,
			autoclose: true,
			endDate: "today"
		})
	});
</script>
<script>
 $(document).ready(function(){
	  $('[data-toggle="tooltip"]').tooltip();
	  initializeJwfSpaInfra();
	});
</script>


							<script>
	   $("#filterDate").click(function () {

	       //-------------------------------

	       var form_data = {
	    		   toDate : $(".toDate").val(),
                   fromDate: $(".fromDate").val(),
           };
           $.ajax({
               url : './dateFilter',
               type : 'GET',

               datatype : 'json',
               data : form_data,
               success : function(message) {

            	   window.location= window.location.href;

               }



           });

	   });

</script>
							<script>
	$( "#runFilterBtn" ).click(function() {


    	    	var Run = "";
    	    	var RunFilter = "";
    	    	var runLength = $("#RunFilter").val().length;

    	    	if(($("#RunFilter").val() == undefined)){
    	    		Run = 0;
    	    	}
    	    	if(runLength == 0 ){
    	    		Run = 0;
    	    	}
    	    	else{
    	    		Run = $("#RunFilter").val();
    	    	}




    var form_data = {
    		RunFilter : Run



    };

    $.ajax({
        url : './runFilter',
        type : 'GET',

        datatype : 'json',
        data : form_data,
        success : function(message) {

        	   window.location= window.location.href;

        }

     });

 });
    </script>

							<script>
document.addEventListener("DOMContentLoaded", function(event) {

	var g1, g2, g3, g4, g5, g6, g7, g17, g18, g16, g19, g20, g21, g22, g23, g24, g25;

  g1 = new JustGage({
    id: "g1",
    decimals: true,
    value: ${recordAnomalyScore},
    min: 0,
    max: 100,
   /*  title: "Custom Width", */
    label: "",
    gaugeWidthScale: 0.5,
    customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
        {"lo":50,"hi":100,"color":"#008000"}],
  });
  g2 = new JustGage({
      id: "g2",
      decimals: true,
      value: ${nullCountScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g3 = new JustGage({
      id: "g3",
      decimals: true,
      value: ${allFieldsScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g4 = new JustGage({
      id: "g4",
      decimals: true,
      value: ${identityFieldsScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g5 = new JustGage({
      id: "g5",
      decimals: true,
      value: ${numericalFieldScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g6 = new JustGage({
      id: "g6",
      decimals: true,
      value: ${stringFieldScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g7 = new JustGage({
      id: "g7",
      decimals: true,
      value: ${recordFieldScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g8 = new JustGage({
      id: "g8",
      decimals: true,
      value: ${ruleScoreDF},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g9 = new JustGage({
      id: "g9",
      decimals: true,
      value: ${dataDriftScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g10 = new JustGage({
      id: "g10",
      decimals: true,
      value: ${totalDQI},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g11 = new JustGage({
      id: "g11",
      decimals: true,
      value: ${TimelinessCheckScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g18 = new JustGage({
      id: "g18",
      decimals: true,
      value: ${lengthCheckScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g17 = new JustGage({
      id: "g17",
      decimals: true,
      value: ${badDataScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g16 = new JustGage({
      id: "g16",
      decimals: true,
      value: ${dateRuleCheckScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
  g19 = new JustGage({
      id: "g19",
      decimals: true,
      value: ${TimelinessCheckScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });

  //for pattern unmatch 11-junly-2019
  g20 = new JustGage({
      id: "g20",
      decimals: true,
      value: ${patternDataScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });
	//for global rules
	g21 = new JustGage({
	      id: "g21",
	      decimals: true,
	      value: ${GlobalruleScoreDF},
	      min: 0,
	      max: 100,
	     /*  title: "Custom Width", */
	      label: "",
	      gaugeWidthScale: 0.5,
	      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
	          {"lo":50,"hi":100,"color":"#008000"}],
	    });


	//for SQL-Rules score
  g22 = new JustGage({
      id: "g22",
      decimals: true,
      value: ${sqlRuleScoreDF},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });

  g23 = new JustGage({
      id: "g23",
      decimals: true,
      value: ${DefaultCheckScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });

  g24 = new JustGage({
      id: "g24",
      decimals: true,
      value: ${lengthCheckScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });

  g25 = new JustGage({
      id: "g25",
      decimals: true,
      value: ${defaultPatternDataScore},
      min: 0,
      max: 100,
     /*  title: "Custom Width", */
      label: "",
      gaugeWidthScale: 0.5,
      customSectors : [{"lo":0,"hi":50,"color":"#f9c802"},
          {"lo":50,"hi":100,"color":"#008000"}],
    });



});
</script>
<script>
   function updateColumnCheckThreshold(idApp,checkName,column_or_rule_name,failPercentage)
             {
            	 var defaultPattern='';
            	 var updated_threshold = parseFloat(parseFloat(failPercentage)  + 0.01).toFixed(2);
            	 msg =  "<span style='color:blue;'><i> Updated threshold for " + checkName + " is " + updated_threshold+ ". Please change to edit the threshold.</i><br> <br> <label>New Threshold Value : &nbsp; </label><input type='text' id='thresholdValue'><br><span style='color:red;'>Note: Please enter value in the range 0-100</span><br><span id='outOfRange' style='color:red;'></span>";
                  Modal.confirmDialog(2, 'Threshold update Status', msg, ['Submit', 'Cancel'], { onDialogClose: sendRequestApi, onDialogLoad: sendRequestApi  });
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

                  function	sendRequestApi(oEvent) {
                      var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;
                      var update_Threshold = $("#thresholdValue").val();
                      switch(sEventId) {
                          case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
                              PageCtrl.debug.log('on Dialog load','ok');
                              break;

                          case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
                              if (sBtnSelected === 'Submit' && update_Threshold<=100 && !isNaN(update_Threshold)) {
                            	  updateThreshold(idApp, checkName, column_or_rule_name, update_Threshold, defaultPattern);
                              }
                              break;
                      }
                 }
             }
     
   function updateRecordCountAnomalyThreshold(idApp,checkName,column_or_rule_name,failPercentage)
     {
    	 var defaultPattern='';
    	 var updated_threshold = parseFloat(parseFloat(failPercentage)  + 0.01).toFixed(2);
    	 msg =  "<span style='color:blue;'><i> Updated threshold for " + checkName + " is " + updated_threshold+ ". Please change to edit the threshold.</i><br> <br> <label>New Threshold Value : &nbsp; </label><input type='text' id='thresholdValue'><br><span style='color:red;'>Note: Please enter value in the range 0-100</span><br><span id='outOfRange' style='color:red;'></span>";
          Modal.confirmDialog(2, 'Threshold update Status', msg, ['Submit', 'Cancel'], { onDialogClose: sendRequestApi, onDialogLoad: sendRequestApi  });
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

          function	sendRequestApi(oEvent) {
              var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;
              var update_Threshold = $("#thresholdValue").val();
              switch(sEventId) {
                  case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
                      PageCtrl.debug.log('on Dialog load','ok');
                      break;

                  case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
                      if (sBtnSelected === 'Submit' && update_Threshold<=100 && !isNaN(update_Threshold)) {
                    	  updateRCAThreshold(idApp, checkName, update_Threshold);
                      }
                      break;
              }
         }
     }

    function updateColumnCheckPatterns(idApp,checkName,column_or_rule_name,patternName,failRecords,totalRecords){
                   var failPercentage= Math.round(failRecords*100/totalRecords);
                   var defaultPattern = 'val:'+patternName;
                   msg =  "<span style='color:blue;'><i> New Pattern selected below will be added for column " + column_or_rule_name + "<br></i></span><span style='color:black;'>" + patternName + "<br></span>";
                   Modal.confirmDialog(2, 'New Pattern Addition', msg, ['Submit', 'Cancel'], { onDialogClose: sendRequestApi, onDialogLoad: sendRequestApi  });
         
                   function	sendRequestApi(oEvent) {
                       var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;
                       switch(sEventId) {
                           case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
                               PageCtrl.debug.log('on Dialog load','ok');
                               break;

                           case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
                               if (sBtnSelected === 'Submit') {
                            	   updateThreshold(idApp, checkName, column_or_rule_name, failPercentage, defaultPattern);
                               }
                               break;
                       }
                  }

           }

           function reloadResultsPage(oEvent) {
           	 window.location.reload();
  		   }

           function updateThreshold(idApp, checkName, column_or_rule_name, failed_Threshold, defaultPattern) {
               var form_data = {
            		   idApp: idApp,
                       checkName:checkName,
                       column_or_rule_name:column_or_rule_name,
                       failed_Threshold:failed_Threshold,
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
                                 var title="";
                                 //toastr.info(msg);

                                 if(checkName === "Default Pattern Check"){
                                   title="New Pattern Addition";
                                   if(status === "failed"){
                                      msg = "Failed to add New Pattern. ";
                                   }
                                   msg = msg+"<span style='color:blue;'>. <i>Changes will be auto approved in validation rule catalog.</i></span>";
                                 } else{
                                    msg = msg + "<span style='color:blue;'>. <i>Changes will be auto approved in validation rule catalog.</i></span>";
                                    title="Threshold update Status";
                                 }

                                 Modal.confirmDialog(0,title, msg, ['Ok'], { onDialogClose: reloadResultsPage });

                           } catch(e) {
                                toastr.info('Failed to update Check Threshold.');
                           }
                       },
                      error: function (textStatus) {
                           toastr.info('Failed to update Check Threshold.');
                      },
                        complete: function(){
                         $("#updatethresholdWaitModel").hide();
                        }
                   });
            }
           
           function updateRCAThreshold(idApp, checkName, failed_Threshold) {
               var form_data = {
            		   idApp: idApp,
                       checkName:checkName,
                       failed_Threshold:failed_Threshold
                   };
                   $.ajax({
                      url: './updateRecordCountAnomalyThreshold',
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
                                 var title="";
                                 //toastr.info(msg);
                                 Modal.confirmDialog(0,title, msg, ['Ok'], { onDialogClose: reloadResultsPage });

                           } catch(e) {
                                toastr.info('Failed to update Check Threshold.');
                           }
                       },
                      error: function (textStatus) {
                           toastr.info('Failed to update Check Threshold.');
                      },
                        complete: function(){
                         $("#updatethresholdWaitModel").hide();
                        }
                   });
            }
</script>

<jsp:include page="downloadCsvReports.jsp" />
</body>
</html>
