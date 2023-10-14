<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <jsp:include page="header.jsp" />
    <jsp:include page="container.jsp" />
    <jsp:include page="checkVulnerability.jsp" />
    <!-- BEGIN CONTENT -->
    <style>
        .SqlRuleQuery {
            width: 100%;
            height: 150px !important;
            border: 1px solid #cccccc;
            padding: 5px;
            overflow: auto;
            font-family: Courier New;
            font-size: 14px;
            resize: vertical;
        }

        div.container {
            width: 80%;
        }

        div.vertical-line {
            width: 1px;
            background-color: silver;
            height: 100%;
            float: left;
            border: 1px ridge silver;
            border-radius: 0px;
        }

        div.col-md-1 {
            width: 3.33333%;
        }

        div.col-md-8 {
            width: 71.66667%;
        }

        .SqlRuleQuery:focus {
            background-color: #f2f2dc;
            border: 2px solid #99d6ff;
        }

        .dataTables_scrollBody{
            overflow-y: hidden !important;
   	 	}

    </style>
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
                        <div class="portlet-title">
                            <div class="caption font-red-sunglo">
                                <span class="caption-subject bold "> Create Dashboard View
                                </span>
                            </div>
                        </div>
                        <div class="portlet-body form">
                            <div class="form-body">
                                <div class="row">
                                    <div class="col-md-4 col-capture">
                                        <div class="form-group form-md-line-input">
                                            <div class="row">
                                                <div class="col-md-12 col-capture">
                                                    <div class="form-group form-md-line-input">
                                                        <input type="text" class="form-control catch-error"
                                                            id="viewNameId">
                                                        <label for="viewNameId">View
                                                            Name*</label>
                                                        <!--  <span class="help-block">Data Set Name </span> -->
                                                    </div>
                                                    <br /> <span class="required"></span>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-12 col-capture">
                                                    <div class="form-group form-md-line-input">
                                                        <input type="text" class="form-control catch-error" id="descid">
                                                        <label for="descid">Description</label>
                                                        <!--  <span class="help-block">Data Set Name </span> -->
                                                    </div>
                                                    <br />
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-12 col-capture">
                                                    <div class="form-group form-md-line-input">
                                                        <select class="form-control js-example-basic-single"
                                                            style="width: 100%" id="dataTemplateId" name="dataTemplate"
                                                            onchange="getIdAppList()" placeholder="">
                                                            <option value="-1">Select Source Template</option>
                                                            <c:forEach var="getlistdatasourcesnameobj"
                                                                items="${getlistdatasourcesname}">
                                                                <option
                                                                    value="${getlistdatasourcesnameobj.idData}_${getlistdatasourcesnameobj.lsName}">
                                                                    ${getlistdatasourcesnameobj.idData}_${getlistdatasourcesnameobj.lsName}
                                                                </option>
                                                            </c:forEach>
                                                        </select><label for="form_control_1">Data Template </label> <br>

                                                    </div>

                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-12 col-capture">
                                                    <div class="form-group form-md-line-input">
                                                        <select class="form-control js-example-basic-single"
                                                            style="width: 100%" id="datasourceid" name="datasource"
                                                            onchange="ruleFilterFunction()" placeholder="">
                                                        </select><label for="form_control_1">Validation </label> <br>

                                                    </div>

                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-12 col-capture" style="padding-top: 20px;">
                                                    <div style="padding-bottom: 10px;">Selected rules from
                                                        templates:</div>
                                                    <div style="height: 200px; overflow-y: auto;" id="divRunTable">
                                                        <table class="table-striped table-bordered table-hover scroll"
                                                            style="width: 100%" id="selectedRuleId">
                                                            <thead>
                                                                <tr>
                                                                    <th>Sr No.</th>
                                                                    <th>Template</th>
                                                                    <th>Selected Rule</th>
                                                                </tr>
                                                            </thead>
                                                        </table>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-1 col-capture">
                                        <div class="form-group form-md-line-input">
                                            <div class="vertical-line" style="height: 458px;"></div>
                                        </div>
                                    </div>
                                    <div class="col-md-7 col-capture" id="viewRulesId">
                                        <div class="form-group form-md-line-input">
                                            <div class="portlet-body">
                                                <div class="table-responsive-sm">
                                                    <div style="padding-bottom: 10px;">
                                                        Rule List of ID App: <b id="idDataVal"></b>
                                                    </div>
                                                    <table class="table table-striped table-bordered  table-hover"
                                                        id="getData" style="width: 100%;">
                                                        <thead>
                                                            <tr>
                                                                <th>Select</th>
                                                                <th>Status</th>
                                                                <th>Rule Reference</th>
                                                                <th>Rule Type</th>
                                                                <th>Rule Name</th>
                                                                <th>Rule Category</th>
                                                                <th>Rule Expression</th>
                                                                <th>Rule Threshold</th>
                                                            </tr>
                                                        </thead>
                                                    </table>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-actions noborder align-center">
                                    <button type="submit" id="addNewRuleBtnId" class="btn blue">Submit</button>
                                </div>
                            </div>
                        </div>
                        <!-- END SAMPLE FORM PORTLET-->
                    </div>
                </div>
            </div>
        </div>
        <!-- END QUICK SIDEBAR -->
    </div>
    <!-- END CONTAINER -->
    <script src="./assets/global/plugins/jquery.min.js" type="text/javascript">

    </script>
    <script src="./assets/global/plugins/jquery.dataTables.min.js" type="text/javascript"></script>
    <jsp:include page="footer.jsp" />
    <script>
        var table;
        var ruleList = [];
        var list = [];
        var selectedRulelist = [];
        var ruleIdList = [];
        var idAppList = [];
        function ruleFilterFunction() {

            var editor;
            var no_error = 1;
            $('.input_error').remove();

            if (no_error) {
                var form_data = {
                    idApp: $("#datasourceid").val()
                };
                $('#getData').DataTable().destroy();
                
                // get ActualWebBasedUrl
                var sAjaxUrlTmpl = "{0}getRulesByIdApp";
            	var sWebAppBaseUrl = getWebAppBaseUrl();
            	var sActualAjaxUrl = sAjaxUrlTmpl.replace('{0}', sWebAppBaseUrl);
            	//alert("sActualAjaxUrl =>"+sActualAjaxUrl);
                //url: "/databuck/getRulesByIdApp"
                
                $.ajax({
                    type: 'GET',
                    url: sActualAjaxUrl,
                    data: form_data,
                    datatype: 'json',
                    success: function (response) {
                        ruleList = response;
                        var id = ($("#datasourceid").val()).split('_')[0];
                        $('#idDataVal').text(id);

                        if (response.length > 0) {
                            var dataSet = response;
                            if ($.fn.DataTable
                                .isDataTable(".table")) {
                                table.destroy();
                            }
                            table = $('#getData')
                                .DataTable(
                                    {
                                        data: dataSet,
                                        scrollX: true,
                                        autoWidth: true,
                                        order: [2,
                                            'asc'],
                                        columns: [{
                                            'targets': 1,
                                            'searchable': false,
                                            'orderable': false,
                                            'className': 'dt-body-center',
                                            'render': function (data, type, full, meta) {
                                                var isChecked = '';
                                                if (selectedRulelist.length > 1) {
                                                    var obj = selectedRulelist.find(data => data.rowId == parseInt(full.rowId));
                                                    if (obj) {
                                                        if (obj.rowId == parseInt(full.rowId)) {
                                                            isChecked = 'checked';
                                                        } else {
                                                            isChecked = '';
                                                        }
                                                    }
                                                }
                                                var input = '<input type="checkbox" id="id_' + full.rowId + '" name="id[]" ' + isChecked + ' value="' + full.rowId + '">';
                                                return input;
                                            }
                                        },
                                        {
                                            'targets': 2,
                                            'searchable': false,
                                            'orderable': false,
                                            'render': function (data, type, full, meta) {
                                                var classLabel = full.activeFlag == true ? "label-success" : "label-danger";
                                                var flag = full.activeFlag == true ? "Active" : "Inactive";
                                                var input = "<button class=\"btn-link\"><span class=\"label " + classLabel + " label-sm\">" + flag + "</span></button>";
                                                return input;
                                            }
                                        },
                                        {
                                            targets: 3,
                                            data: "ruleReference"
                                        },
                                        {
                                            targets: 4,
                                            data: "ruleType"
                                        },
                                        {
                                            targets: 5,
                                            data: "columnName"
                                        },
                                        {
                                            targets: 6,
                                            data: "ruleCategory"
                                        },
                                        {
                                            targets: 7,
                                            data: "ruleExpression"
                                        },
                                        {
                                            targets: 8,
                                            data: "threshold"
                                        }
                                        ]
                                    });
                        } else {
                            $('.table').DataTable({
                                data: [],
                                scrollX: true,
                                autoWidth: true
                            });
                        }
                    },
                    error: function (error) {
                        console.log('error: ', error);
                    }
                });
            }
        }
        function getIdAppList() {
        	
        	//get ActualwebBasedUrl
        	var sAjaxUrlTmpl = "{0}getListOfIdApp?idData={1}";
        	var sDataTmplid = ($("#dataTemplateId").val()).split('_')[0] ;
        	var sWebAppBaseUrl = getWebAppBaseUrl();
        	var sActualAjaxUrl = sAjaxUrlTmpl.replace('{0}', sWebAppBaseUrl).replace('{1}', sDataTmplid);
        	//alert("sActualAjaxUrl =>"+sActualAjaxUrl);
        	// url: "/databuck/getListOfIdApp?idData=" + ($("#dataTemplateId").val()).split('_')[0]
        	
            $.ajax({
                type: 'GET',
                url: sActualAjaxUrl ,
                success: function (response) {
                    var $select = $("#datasourceid");
                    $select.find("option").remove();
                    $("<option>").val('-1').text(
                        'Please select validation')
                        .appendTo($select);
                    $
                        .each(
                            response,
                            function (index,
                                idAppObj) {
                                $(
                                    "<option>")
                                    .val(
                                        idAppObj.idApp)
                                    .text(
                                        idAppObj.laName)
                                    .appendTo(
                                        $select);
                            });
                },
                error: function (error) {
                    console.log('error: ', error);
                },
                complete: function (complete) {
                }
            })
        }

        $(document).ready(function () {
            $(document).on('click', 'input[type="checkbox"]', function () {
                if (this.checked) {
                    var id = $(this).attr('id').split('id_')[1];
                    list.push(($("#dataTemplateId").val()).split('_')[0]);
                    ruleIdList.push(id);
                    var obj = ruleList.find(data => data.rowId == parseInt(id));
                    idAppList.push(obj.idApp);
                    obj.idData = ($("#dataTemplateId").val()).split('_')[0];
                    obj.laName = ($("#dataTemplateId").val()).split("_").slice(1).join("_");;
                    selectedRulelist.push(obj);
                    $("#selectedRuleTbody").remove();
                    tableBody = $("#selectedRuleId");
                    markup = ""
                    if (selectedRulelist.length >= 1) {
                        $(selectedRulelist).each(function (index, item) {
                            markup = markup + "<tr><td>" + (index + 1) + "</td><td>" + item.laName + "</td><td>" + item.columnName + "</td></tr>"
                        });
                    }

                    tableBody.append("<tbody id=\"selectedRuleTbody\">" + markup + "</tbody>");
                } else {
                    $("#selectedRuleTbody").remove();
                    var id = $(this).attr('id').split('id_')[1];
                    var ind = selectedRulelist.findIndex(data => data.rowId == parseInt(id));
                    selectedRulelist.splice(ind, 1);

                    var idDataobj = list.findIndex(data => data == ($("#dataTemplateId").val()).split('_')[0]);
                    list.splice(idDataobj, 1);

                    var ruleIdobj = ruleIdList.findIndex(data => data == id);
                    ruleIdList.splice(ruleIdobj, 1);

                    var idAppobj = idAppList.findIndex(data => data == ($("#datasourceid").val()).split('_')[0]);
                    idAppList.splice(idAppobj, 1);

                    markup = ""
                    $(selectedRulelist).each(function (index, item) {
                        markup = markup + "<tr><td>" + (index + 1) + "</td><td>" + item.laName + "</td><td>" + item.columnName + "</td></tr>"
                    });
                    tableBody.append("<tbody id=\"selectedRuleTbody\">" + markup + "</tbody>");
                }
            });

            $('#addNewRuleBtnId').click(function (params) {
                var no_error = 1;
                $('.input_error').remove();
                if(!checkInputText()){
    				alert('Vulnerability in submitted data, not allowed to submit!');
    				return;
    			}
                if ($("#viewNameId").val().length === 0) {
                    $('<span class="input_error" style="font-size:12px;color:red">Please enter View Name</span>')
                        .insertAfter($('#viewNameId'));
                    no_error = 0;
                }
                if (ruleIdList.length == 0) {
                    $('<span class="input_error" style="font-size:12px;color:red">Please select atleast one rule</span>')
                        .insertAfter($('#selectedRuleId'));
                    no_error = 0;
                }
                if (ruleIdList.length > 100) {
                    $('<span class="input_error" style="font-size:12px;color:red">Max 100 rules can be selected</span>')
                        .insertAfter($('#divRunTable'));
                    no_error = 0;
                }
                if (no_error) {
                    var form_Data = {
                        viewName: $('#viewNameId').val(),
                        description: $('#descid').val(),
                        idDatas: list.join(),
                        ruleList: ruleIdList.join(),
                        idApps: idAppList.join()
                    };
                    $.ajax({
                        type: 'POST',
                        url: 'saveDashboardView',
                        headers: {'token':$("#token").val()},
                        data: form_Data,
                        success: function (response) {
                            var j_obj = $.parseJSON(response);
                            if (j_obj.hasOwnProperty('success')) {
                                toastr.info(j_obj.success);
                                window.location.href = 'dashboardViewRules';
                            } else {
                                toastr.info(j_obj.failed);
                            }
                        },
                        error: function (error) {
                            console.log('error: ', error);
                        },
                        complete: function (complete) {
                        }
                    })
                }
            })
        });
    </script>