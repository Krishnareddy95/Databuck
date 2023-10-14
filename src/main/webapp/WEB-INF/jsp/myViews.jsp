<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@page import="com.databuck.service.RBACController" %>
        <jsp:include page="header.jsp" />
        <jsp:include page="checkVulnerability.jsp" />
        <jsp:include page="container.jsp" />
        <style>
            .displayDiv {
                display: none;
            }

            table.dataTable tbody th,
            table.dataTable tbody td {
                white-space: nowrap;
            }

            .dataTables_scrollBody {
                overflow-y: hidden !important;
            }
        </style>
        <!-- BEGIN CONTENT -->
        <div class="page-content-wrapper">
            <!-- BEGIN CONTENT BODY -->
            <div class="page-content">
                <!-- BEGIN PAGE TITLE-->
                <!-- END PAGE TITLE-->
                <!-- END PAGE HEADER-->
                <div class="row">
                    <div class="col-md-6" style="width: 100%;">
                        <!-- BEGIN SAMPLE FORM PORTLET-->
                        <div class="portlet light bordered init">

                            <div class="cotainer">

                                <div class="tabbable tabbable-custom boxless tabbable-reversed">
                                    <ul class="nav nav-tabs" style="border-bottom: 1px solid #ddd;">
                                        <li style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="executiveSummary"
                                                style="padding: 2px 2px;"><strong>Executive
                                                    Summary</strong></a></li>

                                        <li style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="dashboard_View"
                                                style="padding: 2px 2px;"><strong>Detailed View</strong></a></li>

                                        <li style="width: 8.63%"><a onClick="validateHrefVulnerability(this)"  href="dqUniverse"
                                                style="padding: 2px 2px;"><strong>DQ
                                                    Universe</strong></a></li>
                                        <li style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="locationInfo"
                                                style="padding: 2px 2px;"><strong>DQ
                                                    Lineage</strong></a></li>
                                        <% boolean flag=false; flag=RBACController.rbac("MyViews", "R" , session); if
                                            (flag) { %>
                                            <li class="active" style="width: 8%"><a onClick="validateHrefVulnerability(this)"  href="myViews"
                                                    style="padding: 2px 2px;"><strong>My Views</strong></a></li>
                                            <% } %>
                                    </ul>
                                </div>
                            </div>


                            <div class="portlet-title">
                                <div class="caption font-red-sunglo">
                                    <span class="caption-subject bold ">My Views </span>
                                </div>
                            </div>
                            <div class="portlet-body">
                                <div class="row">
                                    <div class="col-md-12 col-capture">
                                        <div class="form-group form-md-line-input">
                                            <select class="form-control js-example-basic-single" style="width: 100%"
                                                id="dashboardViewId" name="viewList" onchange="getDetailsByViewId()">
                                                <option value="-1">Select View Name</option>
                                                <c:forEach var="viewRuleVal" items="${listViewRules}">
                                                    <option value="${viewRuleVal.idruleMap}">
                                                        ${viewRuleVal.viewName}</option>
                                                </c:forEach>
                                            </select><label for="form_control_1">View Name </label> <br>

                                        </div>

                                    </div>
                                </div>
                            </div>
                            <!-- Started section from dashboard -->
                            <div class="Row" style="padding-top: 35px;">
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
                            </div>
                            <div class="Row">
                                <div class="col-sm-9">
                                    <div class="bootstrap-iso">
                                        <div class="container-fluid">
                                            <div class="row">
                                                <div class="col-md-12 col-sm-12 col-xs-12">
                                                    <form id="tokenForm1" action=""
                                                        class="form-horizontal" method="post">
                                                        <div class="form-group ">

                                                            <div class="col-sm-10">
                                                                <div class="input-group">

                                                                    <div class="Row">
                                                                        <div class="col-md-5 col-sm-5">
                                                                            <input class="toDate" id="date" name="date"
                                                                                placeholder="From Date" type="text"
                                                                                value="${toDate}" />
                                                                        </div>
                                                                        <div class="col-md-5 col-sm-5">
                                                                            <input class="fromDate" id="date"
                                                                                name="date" placeholder="To date"
                                                                                type="text" value="${fromDate}" />
                                                                        </div>
                                                                        <div class="col-md-2 col-sm-2">
                                                                            <input type="button" id="filterDate"
                                                                                class="btn btn-denger" value="Search"
                                                                                style="font-size: 12px; width: 80px; height: 25px; text-align: center; font-weight: bold;">
                                                                        </div>
                                                                    </div>

                                                                </div>
                                                            </div>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="hidden" id="divdiv" style="margin-top: 30px;">
                                <input type="hidden" id="nullSummaryTableName" value="DATA_QUALITY_NullCheck_Summary">
                                <div id="loading-progress" class="ajax-loader hidden">
                                    <img src="${pageContext.request.contextPath}/assets/global/img/loading-circle.gif"
                                        class="img-responsive" />
                                </div>
                                <div id="allVals"></div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="downloadCsvReports.jsp" />
        <jsp:include page="footer.jsp" />

        <head>
            <script src="./assets/global/plugins/jquery.min.js" type="text/javascript"></script>
            <script src="./assets/global/plugins/jquery.dataTables.min.js" type="text/javascript"></script>
            <link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
            <script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
            <!-- Include Date Range Picker -->
            <script type="text/javascript" src="./assets/global/plugins/bootstrap-datepicker.min.js"></script>
            <link rel="stylesheet" href="./assets/global/plugins/bootstrap-datepicker3.css" />
        </head>
        <script>
                $(document).ready(function() {
                	$('#tokenForm1').ajaxForm({
                		headers: {'token':$("#token").val()}
                	});
                });
        </script>
        <script>
            var table;
            function getDetailsByViewId() {
                var form_Data = {
                    idruleMap: $('#dashboardViewId').val()
                };

                $.ajax({
                    type: 'GET',
                    url: 'getDataForIdApps',
                    data: form_Data,
                    beforeSend: function () {
                        $('#loading-progress').removeClass(
                            'hidden').show();
                    },
                    success: function (response) {
                        $('#divdiv').removeClass('hidden');
                        var idAppList = response.idApp.split(',');
                        var flags = [], output = [], l = idAppList.length, i;
                        for (i = 0; i < l; i++) {
                            if (flags[idAppList[i]]) continue;
                            flags[idAppList[i]] = true;
                            output.push(idAppList[i]);
                        };
                        var tableName = $("#nullSummaryTableName").val();
                        var idApp = idAppList[0];
                        initializeJwfSpaInfra(); // Load JWF framework module on this page
                        var stringVal = "";
                        $('#allVals').empty();
                        stringVal = " <div class=\"portlet-title\">"
                            + "	<div class=\"caption font-red-sunglo\">"
                            + " <span class=\"caption-subject bold \">Null Check Summary </span>"
                            + " </div>"
                            + " </div>"
                            + " <hr />"
                            + " <div class=\"portlet-body\">"
                            + " <table id=\"ColNullSummaryTable\""
                            + " class=\"table table-striped table-bordered  table-hover\""
                            + " style=\"width: 100%;\">"
                            + " <thead>"
                            + " <tr>"
                            + " <th>IdApp</th>"
                            + " <th>Execution_Date</th>"
                            + " <th>Run</th>"
                            + " <th>Status</th>"
                            + " <th>Col_Name</th>"
                            + " <th>Null_Value</th>"
                            + " <th>Record_Count</th>"
                            + " <th>Null_Percentage</th>"
                            + " <th>Null_Threshold</th>"
                            + " <th>Action</th>"
                            + " <th>Historic_Null_Mean</th>"
                            + " <th>Historic_Null_Stddev</th>"
                            + " <th>Historic_Null_Status</th>"
                            + " <th>Download File</th>"
                            + " </tr>"
                            + " </thead>"
                            + " </table>"
                            + " </div>"
                        $('#allVals').append(stringVal);
                        alliTotalDisplayRecords = 0;
                        allITotalRecords = 0;
                        allOutput = [];
                        if ($.fn.DataTable.isDataTable("#ColNullSummaryTable")) {
                            table.fnDestroy();
                        }
                        //get ActualWebBasedUrl 
                        var sAjaxUrlTmpl = "{0}ColNullSummTableForNullTab?tableName={1}";
        				var sTableName = tableName ;
        				var sWebAppBaseUrl = getWebAppBaseUrl();
        				var sActualAjaxUrl = sAjaxUrlTmpl.replace('{0}', sWebAppBaseUrl).replace('{1}', sTableName);
        				//alert("sActualAjaxUrl =>"+sActualAjaxUrl);
        				// url: "/databuck/ColNullSummTableForNullTab?tableName=" + tableName + 
        	
                        output.forEach((id, index) => {
                            $.ajax({
                                type: 'GET',
                                url: sActualAjaxUrl + "&idApp=" + id
                                    + "&sEcho=1&iColumns=13&sColumns=%2C%2C%2C%2C%2C%2C%2C%2C%2C%2C%2C%2C&iDisplayStart=0&iDisplayLength=10&mDataProp_0=0&sSearch_0=&bRegex_0=false&bSearchable_0=true&bSortable_0=true&mDataProp_1=1&sSearch_1=&bRegex_1=false&bSearchable_1=true&bSortable_1=true&mDataProp_2=2&sSearch_2=&bRegex_2=false&bSearchable_2=true&bSortable_2=true&mDataProp_3=3&sSearch_3=&bRegex_3=false&bSearchable_3=true&bSortable_3=true&mDataProp_4=4&sSearch_4=&bRegex_4=false&bSearchable_4=true&bSortable_4=true&mDataProp_5=5&sSearch_5=&bRegex_5=false&bSearchable_5=true&bSortable_5=true&mDataProp_6=6&sSearch_6=&bRegex_6=false&bSearchable_6=true&bSortable_6=true&mDataProp_7=7&sSearch_7=&bRegex_7=false&bSearchable_7=true&bSortable_7=true&mDataProp_8=8&sSearch_8=&bRegex_8=false&bSearchable_8=true&bSortable_8=true&mDataProp_9=9&sSearch_9=&bRegex_9=false&bSearchable_9=true&bSortable_9=true&mDataProp_10=10&sSearch_10=&bRegex_10=false&bSearchable_10=true&bSortable_10=true&mDataProp_11=11&sSearch_11=&bRegex_11=false&bSearchable_11=true&bSortable_11=true&mDataProp_12=12&sSearch_12=&bRegex_12=false&bSearchable_12=true&bSortable_12=true&sSearch=&bRegex=false&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&_=1618990850438",
                                success: function (response) {
                                    response1 = response;
                                    allITotalRecords = allITotalRecords + response1.iTotalRecords;
                                    alliTotalDisplayRecords = alliTotalDisplayRecords + response1.iTotalDisplayRecords;
                                    if (response1.aaData.length > 0) {
                                        for (var i = 0; i < response1.aaData.length; i++) {
                                            response1.aaData[i].splice(0, 0, id);
                                            allOutput.push(response1.aaData[i]);
                                        }
                                    }
                                    if ($.fn.DataTable.isDataTable("#ColNullSummaryTable")) {
                                        table.fnDestroy();
                                    }
                                    table = $('#ColNullSummaryTable').dataTable(
                                        {
                                            "bPaginate": true,
                                            "order": [0, 'asc'],
                                            'sScrollX': true,
                                            'aaData': allOutput
                                        });
                                },
                                error: function (error) {
                                    console.log('error: ', error);
                                },
                                complete: function () {
                                    $('#loading-progress').addClass(
                                        'hidden');
                                }
                            });
                        });
                    },
                    error: function (error) {
                        console.log('error: ', error);
                    }
                });
            }
        </script>
        <script>
            $(document).ready(function () {
                var date_input = $('input[name="date"]'); //our date input has the name "date"
                var container = $('.bootstrap-iso form').length > 0 ? $('.bootstrap-iso form').parent() : "body";
                date_input.datepicker({
                    format: 'yyyy-mm-dd',
                    container: container,
                    todayHighlight: true,
                    autoclose: true,
                    endDate: "today"
                })
            });
            $("#filterDate").click(function () {
                var form_data = {
                    toDate: $(".toDate").val(),
                    fromDate: $(".fromDate").val(),
                };
                $.ajax({
                    url: './dateFilter',
                    type: 'GET',
                    datatype: 'json',
                    data: form_data,
                    success: function (message) {
                        getDetailsByViewId();
                    }
                });
            });
        </script>