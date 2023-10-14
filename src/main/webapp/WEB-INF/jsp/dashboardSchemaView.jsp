
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />

    <script type="text/javascript" src="./assets/global/plugins/jsapi.js"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {

        var data = google.visualization.arrayToDataTable([
            ['Schema Dashboard', 'Number of tables tested', 'Number of tables failed', 'Threshold', { role: 'annotation' } ],
            ['Record Count Anomaly', 150, 50, 60, ''],
            ['Null Count', 150, 100, 60, ''],
            ['All fields - duplicate rows', 150, 100, 60, ''],
            ['Identity fields - duplicate rows', 150, 50, 60, ''],
            ['Numerical Field Stats', 150, 50, 60, ''],
            ['String Field Stats', 150, 50, 60, '']
          ]);

            // Set chart options
            // var options = {'title':'How Much Pizza I Ate Last Night'};
            var options = {
                    legend: { position: 'top', maxLines: 3 },
                    bar: { groupWidth: '75%' },
                    isStacked: true
                };

            // Instantiate and draw our chart, passing in some options.
            var chart = new google.visualization.ColumnChart(document.getElementById('dashboard_status_div'));
            chart.draw(data, options);


        var data = google.visualization.arrayToDataTable([
          ['Date', 'Record Count'],
          ['10/20/2016',  950],
          ['10/21/2016',  1050],
          ['10/22/2016',  1070],
          ['10/23/2016',  2000],
          ['10/24/2016',  2000],
          ['10/25/2016',  2000],
          ['10/26/2016',  2000],
          ['10/27/2016',  2500]
        ]);

        var options = {
            title: 'Summary - Record Count Anomaly',
            hAxis: {title: 'Date',  titleTextStyle: {color: '#333'}},
            vAxis: {minValue: 0},
            height: 250,
            width: 1000
        };

        var chart = new google.visualization.AreaChart(document.getElementById('record_summary_div'));
        chart.draw(data, options);

        var data = google.visualization.arrayToDataTable([
          ['Date', 'Null Count'],
          ['10/20/2016',  650],
          ['10/21/2016',  850],
          ['10/22/2016',  870],
          ['10/23/2016',  1000],
          ['10/24/2016',  500],
          ['10/25/2016',  400],
          ['10/26/2016',  100],
          ['10/27/2016',  3000]
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

        var data = google.visualization.arrayToDataTable([
          ['Date', 'Duplicates'],
          ['10/20/2016',  100],
          ['10/21/2016',  200],
          ['10/22/2016',  300],
          ['10/23/2016',  400],
          ['10/24/2016',  500],
          ['10/25/2016',  400],
          ['10/26/2016',  100],
          ['10/27/2016',  300]
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
          ['10/20/2016',  50],
          ['10/21/2016',  250],
          ['10/22/2016',  980],
          ['10/23/2016',  450],
          ['10/24/2016',  550],
          ['10/25/2016',  440],
          ['10/26/2016',  190],
          ['10/27/2016',  1020]
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
          ['10/20/2016',  250],
          ['10/21/2016',  350],
          ['10/22/2016',  180],
          ['10/23/2016',  50],
          ['10/24/2016',  550],
          ['10/25/2016',  440],
          ['10/26/2016',  900],
          ['10/27/2016',  990]
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
          ['10/20/2016',  70],
          ['10/21/2016',  80],
          ['10/22/2016',  90],
          ['10/23/2016',  50],
          ['10/24/2016',  550],
          ['10/25/2016',  440],
          ['10/26/2016',  900],
          ['10/27/2016',  990]
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
      }
    </script>
    <!-- BEGIN CONTENT -->
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
                    <!-- <div class="row">
                        <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
                            <div class="dashboard-stat2 bordered">
                                <div class="display">
                                    <div class="number">
                                        <h3 class="font-red-haze">
                                            <span data-counter="counterup" data-value="1349"><span class="label label-success label-sm"> Passed </span></span>
                                        </h3>
                                        <small>Record Count Anomaly</small>
                                    </div>
                                </div>
                                <div class="progress-info">
                                    <div class="progress">
                                        <span style="width: 100%;" class="progress-bar progress-bar-success green">
                                            <span class="sr-only"></span>
                                        </span>
                                    </div>
                                    <div class="status">
                                        <div class="status-title"> Tables tested - 150, Tables failed - 50 </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
                            <div class="dashboard-stat2 bordered">
                                <div class="display">
                                    <div class="number">
                                        <h3 class="font-red-haze">
                                            <span data-counter="counterup" data-value="1349"><span class="label label-danger label-sm"> Failed </span></span>
                                        </h3>
                                        <small>All Fields - Duplicate Rows</small>
                                    </div>
                                </div>
                                <div class="progress-info">
                                    <div class="progress">
                                        <span style="width: 100%;" class="progress-bar progress-bar-danger red-haze">
                                            <span class="sr-only"></span>
                                        </span>
                                    </div>
                                    <div class="status">
                                        <div class="status-title"> Tables tested - 150, Tables failed - 100 </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
                            <div class="dashboard-stat2 bordered">
                                <div class="display">
                                    <div class="number">
                                        <h3 class="font-red-haze">
                                            <span data-counter="counterup" data-value="1349"><span class="label label-success label-sm"> Passed </span></span>
                                        </h3>
                                        <small>Numerical Field Stats</small>
                                    </div>
                                </div>
                                <div class="progress-info">
                                    <div class="progress">
                                        <span style="width: 100%;" class="progress-bar progress-bar-success green">
                                            <span class="sr-only"></span>
                                        </span>
                                    </div>
                                    <div class="status">
                                        <div class="status-title"> Tables tested - 150, Tables failed - 5 </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>

                    <div class="row">
                        <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
                            <div class="dashboard-stat2 bordered">
                                <div class="display">
                                    <div class="number">
                                        <h3 class="font-red-haze">
                                            <span data-counter="counterup" data-value="1349"><span class="label label-danger label-sm"> Failed </span></span>
                                        </h3>
                                        <small>Null Count</small>
                                    </div>
                                </div>
                                <div class="progress-info">
                                    <div class="progress">
                                        <span style="width: 100%;" class="progress-bar progress-bar-danger red-haze">
                                            <span class="sr-only"></span>
                                        </span>
                                    </div>
                                    <div class="status">
                                        <div class="status-title"> Tables tested - 150, Tables failed - 100 </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
                            <div class="dashboard-stat2 bordered">
                                <div class="display">
                                    <div class="number">
                                        <h3 class="font-red-haze">
                                            <span data-counter="counterup" data-value="1349"><span class="label label-success label-sm"> Passed </span></span>
                                        </h3>
                                        <small>Identity Fields - Duplicate Rows</small>
                                    </div>
                                </div>
                                <div class="progress-info">
                                    <div class="progress">
                                        <span style="width: 100%;" class="progress-bar progress-bar-success green">
                                            <span class="sr-only"></span>
                                        </span>
                                    </div>
                                    <div class="status">
                                        <div class="status-title"> Tables tested - 150, Tables failed - 5 </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
                            <div class="dashboard-stat2 bordered">
                                <div class="display">
                                    <div class="number">
                                        <h3 class="font-red-haze">
                                            <span data-counter="counterup" data-value="1349"><span class="label label-danger label-sm"> Failed </span></span>
                                        </h3>
                                        <small>String Field Stats</small>
                                    </div>
                                </div>
                                <div class="progress-info">
                                    <div class="progress">
                                        <span style="width: 100%;" class="progress-bar progress-bar-danger red-haze">
                                            <span class="sr-only"></span>
                                        </span>
                                    </div>
                                    <div class="status">
                                        <div class="status-title"> Tables tested - 150, Tables failed - 100 </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div> -->

                    

        <div class="row">
            <div class="col-md-12">
                <!-- BEGIN EXAMPLE TABLE PORTLET-->
                <div class="portlet light bordered">
                    <div class="portlet-title">
                        <div class="caption font-red-sunglo">
                            <span class="caption-subject bold ">Schema Dashboard : </span>
                        </div>
                    </div>
                    <div class="portlet-body">
                    <input type="hidden" id="tourl" value="">
                    
                    <!--Div that will hold the chart-->
                    <div id="dashboard_status_div" style="height: 300px; width:100%; margin: 0 auto;">
                        
                    </div>

                    <div class="portlet-body">
                        <table class="table table-striped table-bordered  table-hover">
                            <thead>
                                <tr>
                                    <th> Test </th>
                                    <th> Status </th>
                                    <th> Key Metrics </th>
                                    <th> Measurement </th>
                                    <th> Key Metrics </th>
                                    <th> Measurement </th>
                                    <th> Summary </th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td class="hidden-xs"> Record Count Anomaly </td>
                                    <td> <span class="label label-success label-sm"> Passed </span> </td>
                                    <td class="hidden-xs"> Record Count </td>
                                    <td class="hidden-xs"> ${recordCount} </td>
                                    <td class="hidden-xs"> Average Record Count </td>
                                    <td class="hidden-xs"> ${averageRecordCount}  </td>
                                    <td>
                                        <a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;" id="record_summary"> <i class="fa fa-line-chart"> Chart</i> </a>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="hidden-xs"> Null Count </td>
                                    <td> <span class="label label-danger label-sm"> Failed </span> </td>
                                    <td class="hidden-xs"> Number of non-null columns </td>
                                    <td class="hidden-xs"> ${nonNullColumns } </td>
                                    <td class="hidden-xs"> Number of records failed </td>
                                    <td class="hidden-xs"> ${nonNullColumnsFailed } </td>
                                    <td>
                                        <a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;" id="null_summary"> <i class="fa fa-line-chart"> Chart</i> </a>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="hidden-xs"> All fields - duplicate rows </td>
                                    <td> <span class="label label-danger label-sm"> Failed </span> </td>
                                    <td class="hidden-xs"> Number of duplicates </td>
                                    <td class="hidden-xs"> ${allFields } </td>
                                    <td class="hidden-xs">  </td>
                                    <td class="hidden-xs">  </td>
                                    <td>
                                        <a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;" id="all_dup_summary"> <i class="fa fa-line-chart"> Chart</i> </a>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="hidden-xs"> Identity fields - duplicate rows </td>
                                    <td> <span class="label label-success label-sm"> Passed </span> </td>
                                    <td class="hidden-xs"> Number of duplicates </td>
                                    <td class="hidden-xs"> ${identityFields } </td>
                                    <td class="hidden-xs">  </td>
                                    <td class="hidden-xs">  </td>
                                    <td>
                                        <a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;" id="id_dup_summary"> <i class="fa fa-line-chart"> Chart</i> </a>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="hidden-xs"> Numerical Field Stats </td>
                                    <td> <span class="label label-success label-sm"> Passed </span> </td>
                                    <td class="hidden-xs"> Number of numerical columns </td>
                                    <td class="hidden-xs"> ${numberofNumericalColumnsYes} </td>
                                    <td class="hidden-xs"> Number of numerical columns failed </td>
                                    <td class="hidden-xs"> ${numberofNumericalColumnsFailed} </td>
                                    <td>
                                        <a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;" id="num_summary"> <i class="fa fa-line-chart"> Chart</i> </a>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="hidden-xs"> String Field Stats </td>
                                    <td> <span class="label label-danger label-sm"> Failed </span> </td>
                                    <td class="hidden-xs"> Number of string columns </td>
                                    <td class="hidden-xs"> ${numberofStringColumnsYes} </td>
                                    <td class="hidden-xs"> Number of string columns failed </td>
                                    <td class="hidden-xs"> ${numberofStringColumnsFailed} </td>
                                    <td>
                                        <a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;" id="str_summary"> <i class="fa fa-line-chart"> Chart</i> </a>
                                    </td>
                                </tr>
                                <tr>
											<td class="hidden-xs">Record Anomaly</td>
											<td><span class="label label-danger label-sm">
													Failed </span></td>
											<td class="hidden-xs">Total Number of Records</td>
											<td class="hidden-xs"> ${recordAnomalyTotal} </td>
											<td class="hidden-xs">Total Number of Records failed</td>
											<td class="hidden-xs">${numberOfRecordsFailed }</td>
											<td><a onClick="validateHrefVulnerability(this)"  class="dashboard-summary" href="javascript:;"
												id="str_summary"> <i class="fa fa-line-chart"> Chart</i>
											</a></td>
										</tr>
                            </tbody>
                        </table>
                    </div>
                    <!--Div that will hold the chart-->
                    <div id="record_summary_div" style="height: 300px; width:100%; margin: 0 auto;" class="dashboard-summary-chart">
                        
                    </div>

                    <!--Div that will hold the chart-->
                    <div id="null_summary_div" style="height: 300px; width:100%; margin: 0 auto;display: none;" class="dashboard-summary-chart">
                        
                    </div>

                    <!--Div that will hold the chart-->
                    <div id="all_dup_summary_div" style="height: 300px; width:100%; margin: 0 auto;display: none;" class="dashboard-summary-chart">
                        
                    </div>

                    <!--Div that will hold the chart-->
                    <div id="id_dup_summary_div" style="height: 300px; width:100%; margin: 0 auto;display: none;" class="dashboard-summary-chart">
                        
                    </div>

                    <!--Div that will hold the chart-->
                    <div id="num_summary_div" style="height: 300px; width:100%; margin: 0 auto;display: none;" class="dashboard-summary-chart">
                        
                    </div>

                    <!--Div that will hold the chart-->
                    <div id="str_summary_div" style="height: 300px; width:100%; margin: 0 auto;display: none;" class="dashboard-summary-chart">
                        
                    </div>

                    <div class="clearfix"></div>

                    <div class="portlet-title">
                        <div class="caption font-red-sunglo">
                            <span class="caption-subject bold ">Schema Tables  <a onClick="validateHrefVulnerability(this)"  href="javascript:;">(Download CSV)</a></span>
                        </div>
                    </div>
                    <hr/>


                    <div class="portlet-body">
                        <table class="table table-striped table-bordered  table-hover">
                            <thead>
                                <tr>
                                    <th> Table Name </th>
                                    <th> Action </th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${listOfDataSources}" var="listOfDataSourcesObj">
									<tr>
									<td class="hidden-xs">${listOfDataSourcesObj.description} </td>
									<td>
									 <a onClick="validateHrefVulnerability(this)"  href="dashboard_table?idData=${listOfDataSourcesObj.idData}"> View Table Results</a>
									</td>
									</tr>
								</c:forEach>
                            </tbody>
                        </table>
                    </div>

                    
                    </div>
                </div>
                <!-- END EXAMPLE TABLE PORTLET-->
            </div>
        </div>
        <!-- close if -->    <!-- close if -->
            </div> 
        </div>           
                                   

<jsp:include page="footer.jsp" />