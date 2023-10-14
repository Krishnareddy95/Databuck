<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <jsp:include page="header.jsp" />
    <jsp:include page="container.jsp" />
    <style>
        table.dataTable tbody th,
        table.dataTable tbody td {
            white-space: nowrap;
        }
        .dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}
    </style>
    <!--============= BEGIN CONTENT BODY============= -->
    <!--*****************      BEGIN CONTENT **********************-->
    <div class="page-content-wrapper">
        <!-- BEGIN CONTENT BODY -->
        <div class="page-content">
            <!-- BEGIN PAGE TITLE-->
            <!-- END PAGE TITLE-->
            <!-- END PAGE HEADER-->
            <div class="row">
                <div class="col-md-12">
                    <!--****         BEGIN EXAMPLE TABLE PORTLET       **********-->
                    <div class="portlet light bordered">
                        <div class="portlet-title">
                            <div class="caption font-red-sunglo">
                                <span class="caption-subject bold ">Views</span>
                            </div>
                        </div>
                        <div class="portlet-body">
                            <div style="padding-bottom: 20px;">
                                <b>Template Table associated with
                                    view:</b>
                            </div>
                            <table class="table table-striped table-bordered  table-hover" style="width: 100%;">
                                <thead>
                                    <tr>
                                        <th>Template Id</th>
                                        <th>Name</th>
                                        <th>Location</th>
                                        <th>Project Name</th>
                                        <th
                                            style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">
                                            Created At</th>
                                        <th>Created By</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="listdatasource" items="${listDataSource}">
                                        <tr>
                                            <td>${listdatasource.idData}</td>
                                            <td>${listdatasource.name}</td>
                                            <td>${listdatasource.dataLocation}</td>
                                            <td>${listdatasource.projectName}</td>
                                            <td
                                                style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">
                                                ${listdatasource.createdAt}</td>
                                            <td>${listdatasource.createdByUser}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <div style="padding-bottom: 20px;padding-top: 20px;">
                                <b>Rule
                                    Table associated with view:</b>
                            </div>

                            <table class="table table-striped table-bordered  table-hover" style="width: 100%;">
                                <thead>
                                    <tr>
                                        <th>RuleId</th>
                                        <th>Rule Name</th>
                                        <th>Rule Type</th>
                                        <th>Rule Category</th>
                                        <th>Expression</th>
                                        <th>Template Name</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="listRuleVal" items="${listOfRules}">
                                        <tr>
                                            <td>${listRuleVal.rowId}</td>
                                            <td>${listRuleVal.columnName}</td>
                                            <td>${listRuleVal.ruleType}</td>
                                            <td>${listRuleVal.ruleCategory}</td>
                                            <td>${listRuleVal.ruleExpression}</td>
                                            <td>${listRuleVal.laName}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                        </div>
                    </div>
                    <!--       *************END EXAMPLE TABLE PORTLET*************************-->
                </div>
            </div>
        </div>
    </div>
    <script src="./assets/global/plugins/jquery.min.js" type="text/javascript"></script>
    <script src="./assets/global/plugins/jquery.dataTables.min.js" type="text/javascript"></script>
    <jsp:include page="footer.jsp" />
    <script>
        $(document).ready(function () {
            var table;
            table = $('.table').dataTable({

                order: [[0, "asc"]],
                "scrollX": true
            });
        });
    </script>