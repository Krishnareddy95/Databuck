<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
    .text-center {
        text-align: center;
    }

    .select_custom {
        margin: 0px 20px;
        border: 1px solid #eee !important;
        text-align: center;
        letter-spacing: 2px;
        vertical-align: middle;
        padding: 5px;
        color: #929292;
    }

    .vertical_align {
        vertical-align: middle !important;
    }

    select:focus {
        outline-width: 0px !important;
    }

    .padding-15-20-10-20 {
        padding: 15px 20px 10px 20px;
    }

    .font_size-15 {
        font-size: 15px;
    }

    .tableCustom {
        font-size: 12px;
        font-family: Arial, Helvetica, sans-serif;
        color: #929292 !important;
        font-weight: inherit;
    }

    .background {
        background-color: #f7f7f7;
    }

    input:focus {
        outline-width: 0px !important;
    }

    textarea:focus {
        outline-width: 0px !important;
        resize: none;
    }

    textarea {
        resize: none;
    }

    input:disabled {
        background: transparent;
    }

    .portlet.light.bordered>.portlet-title.nowLine {
        border-bottom: 0px solid #eef1f5;
        padding-top: 10px;
    }

    .padding-3-10 {
        padding: 3px 3px 3px 10px !important;
    }

    .padding-6-3-0-10 {
        padding: 6px 3px 0px 10px !important
    }

    .td-style {
        padding: 0rem !important;
        width: 100px;
        height: 28px;
    }

    .color-red {
        color: red;
        font-size: 11px !important;
        font-weight: 600;
        vertical-align: middle !important;
    }

    .color-yellow {
        color: #f0bc31;
        font-size: 11px !important;
        font-weight: 600;
        vertical-align: middle !important;
    }

    .color-green {
        color: green;
        font-size: 11px !important;
        font-weight: 600;
        vertical-align: middle !important;
    }

    .input-style {
        border: none;
        text-align: center;
        padding: 0rem;
        width: 100px;
        height: 27px;
    }

    .font-size-11 {
        font-size: 11px;
    }

    .padding-0-20 {
        padding: 0px 20px;
    }

    .padding-3 {
        padding: 3px !important;
    }

    .td_order {
        padding: 0rem !important;
        width: 100px;
        height: 28px;
    }

    .td_displayName {
        padding: 0px 0px 0px 10px !important;
        height: 28px;
    }

    .displayName_input {
        border: none;
        width: 100%;
        height: 100%;
    }

    .order_input {
        width: 93px;
        border: none;
        text-align: center;
        width: 100%;
        height: 100%;
    }

    .margin_bottom-0 {
        margin-bottom: 0px !important;
    }

    .margin_top-20 {
        margin-top: 20px;
    }

    .float_right {
        float: right;
    }

    .min_height-10 {
        min-height: 10px !important;
    }

    .padding_bottom-0 {
        padding-bottom: 0px !important;
    }

    .margin_top-15 {
        margin-top: 15px;
    }

    .template_width {
        width: 213px !important;
    }

    .work_break {
        word-break: break-word;
    }

    .width-170 {
        width: 170px !important;
    }

    /*  accordian */
    .accordion-val {
        cursor: pointer;
        padding: 20px 15px 0px 10px;
        width: 100%;
        border: none;
        border-bottom: 1px solid black !important;
        text-align: left;
        outline: none;
        font-size: 15px;
        font-weight: 600;
        transition: 0.4s;
        height: 45px;
        background-color: white;
    }

    .accordion-val:after {
        content: '\2303';
        color: #777;
        font-weight: initial;
        float: right;
        margin-left: 5px;
        font-size: x-large;
    }

    .active-val:after {
        content: "\2304";
        margin-top: -18px;
    }

    .panel-val {
        padding: 0 18px;
        background-color: white;
        max-height: 0;
        overflow: hidden;
        transition: max-height 0.2s ease-out;
    }
</style>
<div class="page-content-wrapper">
    <div class="page-content">
        <div class="portlet light bordered init">
            <div class="portlet-title">
                <div class="caption font-red-sunglo" style="width: 90%;">
                    <span class="caption-subject bold">Connection Definition</span>
                </div>
                <button class="btn btn-primary float_right" id="saveDashConfig">Save</button>
            </div>
            <div class="portlet-body form">
                <div class="row padding-0-20">
                    <div class="col-md-12">
                        <label class="font_size-15">Select Domain-Project</label> <select id="projectList"
                            class="select_custom">
                            <option disabled selected value="null">Please select
                                Domain-Project</option>
                        </select>
                    </div>
                </div>
                <div class="padding-15-20-10-20">
                    <h5>Connection List (max 6 selection)</h5>
                    <table class="tableCustom table-sm table table-bordered" id="connectionListTable">
                        <thead>
                            <tr class="text-center">
                                <td class="padding-3">Enable</td>
                                <td class="padding-3">Order</td>
                                <td class="padding-3">Connection Name</td>
                                <td class="padding-3">Display Name (max 20 characters)</td>
                            </tr>
                        </thead>

                    </table>
                </div>

            </div>
            <div class="portlet-title nowLine margin_bottom-0">
                <div class="caption font-red-sunglo">
                    <span class="caption-subject bold">Color Grading</span>
                </div>
            </div>
            <div class="portlet-body form">
                <div class="row padding-0-20">
                    <div class="col-md-6">
                        <table class="tableCustom table-sm table table-bordered" id="colorGradingTable">
                            <thead>
                                <tr class="text-center">
                                    <td class="padding-3">Color</td>
                                    <td class="padding-3">Logic</td>
                                    <td class="padding-3">Percentage</td>
                                </tr>
                            </thead>
                        </table>
                    </div>
                </div>

            </div>
            <div class="portlet-title nowLine margin_bottom-0 min_height-10">
                <div class="caption font-red-sunglo padding_bottom-0">
                    <span class="caption-subject bold">Connection-Validation Map</span>
                </div>
            </div>
            <div class="portlet-body form">
                <div class="padding-0-20" id="connection-acc"></div>
            </div>
            <div class="portlet-title nowLine margin_bottom-0 margin_top-20">
                <div class="caption font-red-sunglo">
                    <span class="caption-subject bold">Check-Component Mapping
                        Reference</span>
                </div>
            </div>
            <div class="portlet-body form">
                <div class="padding-0-20">
                    <table class="tableCustom table-sm table table-bordered" id="checkComponentMapping">
                        <thead>
                            <tr class="text-center">
                                <td class="padding-3">Check Name</td>
                                <td class="padding-3">Check Description</td>
                                <td class="padding-3">Component</td>
                            </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>

            </div>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp" />
<script>
    var colorGradingList = [];
    var connectionForDashboardList = [];
    var list = [];
    var connectionValidationMapList = [];
    $(document).ready(function () {
        // Initial API calls
        $.ajax({
            type: 'GET',
            url: 'dbconsole/getAllDomainProjectsForUser',
            datatype: 'json',
            success: function (response) {
                $(response)
                    .each(
                        function (i, item) {
                            $('#projectList')
                                .append(
                                    $(
                                        '<option>',
                                        {
                                            value: item.domainId + '__' + item.idProject,
                                            text: item.domainName + '-' + item.projectName
                                        }));
                        });
            },
            error: function (error) {
                console.log('error: ', error);
            },
            complete: function (complete) {
            }
        });
        $.ajax({
            type: 'GET',
            url: 'dbconsole/getAllCheckComponents',
            datatype: 'json',
            success: function (response) {
                markup = "";
                $(response).each(function (i, item) {
                    markup = markup + "<tr><td class=\"padding-3-10 background\">" + item.checkName + "</td><td class=\"padding-3-10 background\">" + item.description + "</td><td class=\"padding-3-10 background\">" + item.component + "</td></tr>"
                });
                tableBody = $("#checkComponentMapping tbody");
                tableBody.append(markup);
            },
            error: function (error) {
                console.log('error: ', error);
            },
            complete: function (complete) {
            }
        });
        $(document).scrollTop(0);

        // When change project name
        $('#projectList').change(function () {
            var domainId = $('#projectList').val().split('__')[0];
            var projectId = $('#projectList').val().split('__')[1];
            $.ajax({
                type: 'GET',
                url: 'dbconsole/getConnectionsForDashboard',
                datatype: 'json',
                data: { domainId: domainId, projectId: projectId },
                success: function (response) {
                    connectionForDashboardList = response;
                    connectionValidationMapList = [];
                    list = [];
                    var listOfAccordianPresent = $('#connection-acc').find('.accordion-val');
                    for (let i = 0; i < listOfAccordianPresent.length; i++) {
                        var ids = $(listOfAccordianPresent[i]).attr('id');
                        $('#' + ids).remove();
                        $('#' + ids).remove();
                    }
                    markup = "";
                    if (response.length > 0) {
                        var maxVal = response.length <= 6 ? response.length : 6;
                        $(response).each(function (i, item) {
                            var displayname = item.displayName != "" && item.displayName != null ? item.displayName : '';
                            var displayOrder = item.displayOrder != 0 ? item.displayOrder : '';
                            markup = markup + "<tr><td class=\"text-center background padding-3\"><input type=\"checkbox\" class=\"enableChk\" id=\"enable" + i
                                + "\" value=\"" + item.connectionId + "\" /></td><td class=\"text-center background td_order\"><input type=\"number\" id=\"orderId" + i
                                + "\" disabled=\"disabled\" class=\"order_input\" min=\"1\" max=\"" + maxVal + "\" value=\"" + displayOrder
                                + "\" /></td><td class=\"background padding-3-10\" id=\"connectionName" + i + "\">" + item.connectionName
                                + "</td><td class=\"background td_displayName\"><input type=\"text\" id=\"displayName" + i
                                + "\" value=\"" + displayname + "\" maxlength=\"20\" class=\"displayName_input\" disabled=\"disabled\" /></td></tr>"
                        })
                    } else {
                        markup = "<tr><td class=\"text-center\" colspan=\"4\">No data available</td></tr>"
                    }
                    tableBody = $("#connectionListTable");
                    $("#connectionListTableTbody").remove();
                    tableBody.append("<tbody id=\"connectionListTableTbody\">" + markup + "</tbody>");
                    for (let i = 0; i < response.length; i++) {
                        $('#orderId' + i)[0].addEventListener("change", function () {
                            if (parseInt($('#orderId' + i).val()) > maxVal) {
                                $('#orderId' + i).val(maxVal);
                            } else if (parseInt($('#orderId' + i).val()) <= 0) {
                                $('#orderId' + i).val("");
                            }
                            checkForOrderNumber(response, i);
                            if (parseInt($('#orderId' + i).val()) != 0 && $('#orderId' + i).val() != "" && $('#displayName' + i).val() != null && $('#displayName' + i).val() != "") {
                                setAccordion($('#orderId' + i).val(), $('#displayName' + i).val(), i);
                            }
                        });
                        $('#displayName' + i)[0].addEventListener("change", function () {
                            if (parseInt($('#orderId' + i).val()) != 0 && $('#orderId' + i).val() != "" && $('#displayName' + i).val() != null && $('#displayName' + i).val() != "") {
                                setAccordion($('#orderId' + i).val(), $('#displayName' + i).val(), i);
                            }
                        });
                        $('#enable' + i)[0].addEventListener("click", function (event) {
                            var idOfCheckbox = $(this).attr('id');
                            var index = idOfCheckbox.split('enable')[1];
                            enableRecord(event.target.checked, index, idOfCheckbox);
                        });
                        if (response[i].enabled == true) {
                            enableRecord(response[i].enabled, i, 'enable' + i);
                        }

                    }

                },
                error: function (error) {
                    console.log('error: ', error);
                },
                complete: function (complete) {
                }
            });
            $.ajax({
                type: 'GET',
                url: 'dbconsole/getColorGrading',
                datatype: 'json',
                data: { domainId: domainId, projectId: projectId },
                success: function (response) {
                    colorGradingList = response;
                    markup = "";
                    $(response).each(function (i, item) {
                        if (i == 0) {
                            markup = markup + "<tbody id=\"colorGradingTableTbody\"><tr><td class=\"padding-3-10 color-red background\">" + item.color + "</td><td class=\"padding-3-10 background\">" + item.logic + "</td><td class=\"td-style\"><input class=\"input-style\" type=\"text\" id=\"red-percent\" value=\"" + item.colorPercentage + "%\"/></td></tr>"
                        }
                        if (i == 1) {
                            markup = markup + "<tr><td class=\"padding-3-10 color-yellow background\">" + item.color + "</td><td class=\"padding-3-10 background\">" + item.logic + "</td><td class=\"td-style\"><input class=\"input-style\" type=\"text\" id=\"yellow-percent\" value=\"" + item.colorPercentage + "%\"/></td></tr>"
                        }
                        if (i == 2) {
                            markup = markup + "<tr><td class=\"padding-3-10 color-green background\">" + item.color + "</td><td class=\"padding-3-10 background\">" + item.logic + "</td><td class=\"td-style background text-center vertical_align\" id=\"green-percent\">" + item.colorPercentage + "%</td></tr></tbody>"
                        }
                    });
                    tableBody = $("#colorGradingTable");
                    $("#colorGradingTableTbody").remove();
                    tableBody.append(markup);
                },
                error: function (error) {
                    console.log('error: ', error);
                },
                complete: function (complete) {
                }
            });

        });

        function enableRecord(event, index, idOfCheckbox) {
            if (event == true) {
                $('#enable' + index)[0].checked = true;
                $('#enable' + index).addClass('isChecked');
                $('#enable' + index).addClass('isChecked');
                $('#orderId' + index).removeAttr('disabled');
                $('#displayName' + index).removeAttr('disabled');
                $('#displayName' + index).parent().removeClass('background');
                $('#enable' + index).parent().removeClass('background');
                list.push(index);
                var idVal = $('#' + idOfCheckbox).val();
                var domainId = $('#projectList').val().split('__')[0];
                var projectId = $('#projectList').val().split('__')[1];
                $.ajax({
                    type: 'GET',
                    url: 'dbconsole/getConnectionValidationMap',
                    datatype: 'json',
                    data: {
                        domainId: domainId,
                        projectId: projectId,
                        connectionId: idVal
                    },
                    success: function (response) {
                        if (response.length > 0) {
                            if (connectionValidationMapList.length > 0) {
                                var ind = connectionValidationMapList.findIndex(res =>
                                    res.response[0].projectId == response[0].projectId && res.response[0].connectionId == response[0].connectionId);
                                if (ind >= 0) {
                                    connectionValidationMapList.splice(ind, 1);
                                }
                                connectionValidationMapList.push({ response, index });
                            } else {
                                connectionValidationMapList.push({ response, index });
                            }
                        }
                        if (parseInt($('#orderId' + index).val()) != 0 && $('#orderId' + index).val() != "" && $('#displayName' + index).val() != null && $('#displayName' + index).val() != "") {
                            setAccordion($('#orderId' + index).val(), $('#displayName' + index).val(), index);
                        }
                    },
                    error: function (error) {
                        console.log('error: ', error);
                    },
                    complete: function (complete) {
                    }
                });
            } else {
                $('#enable' + index)[0].checked = false;
                $('#orderId' + index).val('');
                $('#enable' + index).removeClass('isChecked');
                $('#orderId' + index).attr('disabled', true);
                $('#displayName' + index).attr('disabled', true);
                $('#displayName' + index).parent().addClass('background');
                $('#enable' + index).parent().addClass('background');
                var ind = list.findIndex(function (data) {
                    return data == index
                });
                list.splice(ind, 1);
                $("#acc_btn" + index).remove();
                $("#acc_panel" + index).remove();
            }
            if (list.length == 6) {
                $(".enableChk").each(function () {
                    if (!$(this).hasClass('isChecked')) {
                        $('#' + $(this).attr('id')).attr('disabled', true);
                    }
                });
            } else {
                $(".enableChk").each(function () {
                    $('#' + $(this).attr('id')).removeAttr('disabled');
                });
            }
        }
    });

    function checkForOrderNumber(response, i) {
        for (let index = 0; index < response.length; index++) {
            if (i !== index) {
                if (parseInt($('#orderId' + i).val()) == parseInt($('#orderId' + index).val())) {
                    $('#orderId' + i).val("")
                }
            }
        }
    }

    // Check the percentage of red and yellow and set accordingly
    $("body").delegate("#red-percent", "change", function () {
        var red_per = parseInt(($('#red-percent').val()).split('%')[0]);
        var yel_per = parseInt(($('#yellow-percent').val()).split('%')[0]);
        if (red_per >= 100) {
            $('#red-percent').val("99%");
            red_per = 99;
        }
        if (red_per <= 0) {
            $('#red-percent').val("1%");
            red_per = 1;
        }
        if (red_per >= yel_per) {
            red_per = parseInt(yel_per) - 1;
            $('#red-percent').val(red_per + "%");
            toastr.info('Percentage should be less than yellow percentage');
        }

        $('#green-percent')[0].innerText = $('#yellow-percent').val();
    });
    $("body").delegate("#yellow-percent", "change", function () {
        var red_per = parseInt(($('#red-percent').val()).split('%')[0]);
        var yel_per = parseInt(($('#yellow-percent').val()).split('%')[0]);
        if (yel_per > 100) {
            $('#yellow-percent').val("100%");
            yel_per = 100;
        }
        if (yel_per <= 1) {
            $('#yellow-percent').val("2%");
            yel_per = 2;
        }
        if (red_per >= yel_per) {
            yel_per = parseInt(yel_per) - 1;
            $('#red-percent').val(yel_per + "%");
            toastr.info('Percentage should be greater than red percentage');
        }

        $('#green-percent')[0].innerText = $('#yellow-percent').val();
    });

    // On click of check box fr enable the connection


    // Check if aleady exits for that accordian and again add 
    var isAccPresent = [];
    function setAccordion(orderVal, displayNameVal, ind) {
        if (isAccPresent.length > 0) {
            var indAc = isAccPresent.findIndex(res => res == ('orderId' + ind + 'displayName' + ind));
            if (indAc < 0) {
                isAccPresent.push('orderId' + ind + 'displayName' + ind);
                setMarup(orderVal, displayNameVal, ind);
            } else {
                $("#acc_btn" + ind).remove();
                $("#acc_panel" + ind).remove();
                setMarup(orderVal, displayNameVal, ind);
            }
        } else {
            isAccPresent.push('orderId' + ind + 'displayName' + ind);
            setMarup(orderVal, displayNameVal, ind);
        }
    }

    // Set html for accordian
    function setMarup(orderVal, displayNameVal, ind) {
        var acc_btn_body_before = "";
        var acc_panel_body_before = "";
        var acc_btn_body_after = "";
        var acc_panel_body_after = "";
        var beforeAcc = false;
        var afterAcc = false;
        var indexOfCurrentAccBeforeList = [];
        var indexOfCurrentAccAfterList = [];
        var listOfAccordianPresent = $('#connection-acc').find('.accordion-val');
        var afterBody = "";
        var beforeBody = "";

        buttonBody = "<button class=\"accordion-val\" id=\"acc_btn" + ind + "\">" + orderVal + " - " + displayNameVal + " (" + $('#connectionName' + ind)[0].innerText + ") </button>"
        var tableTbody = "";
        var currentAcc = connectionValidationMapList.find(res => parseInt(res.index) == ind);
        for (let i = 0; i < listOfAccordianPresent.length; i++) {
            var ids = $(listOfAccordianPresent[i]).attr('id');
            var textOfButton = $('#' + ids).text();
            var numberOfOrder = textOfButton.split(" ")[0];
            if (parseInt(orderVal) > parseInt(numberOfOrder)) {
                beforeAcc = true;
                indexOfCurrentAccBeforeList.push(ids.split('acc_btn')[1]);
            } else if (parseInt(orderVal) < parseInt(numberOfOrder)) {
                afterAcc = true;
                indexOfCurrentAccAfterList.push(ids.split('acc_btn')[1]);
            }
        }

        if (currentAcc != undefined) {
            $(currentAcc.response).each(function (i, item) {
                var dataSourceVal = item.datasource != null ? item.datasource : "Data Source 1";
                var sourceVal = item.source != null ? item.source : "Source " + (i + 1);
                var fileNameVal = item.fileName != null ? item.fileName : item.validationName;
                tableTbody = tableTbody + "<tr><td class=\"padding-3 background work_break\">" + item.validationName + "</td><td class=\"padding-3 background work_break\">" + item.templateName
                    + "</td><td class=\"padding-3 text-center work_break\"><textarea maxlength=\"50\" class=\"displayName_input\" id=\"dataSource" + i + '_' + item.idApp + '_' + item.conAppId + "\">" + dataSourceVal
                    + "</textarea></td><td class=\"padding-3 text-center work_break\"><textarea maxlength=\"50\" class=\"displayName_input\" id=\"source" + i + '_' + item.idApp + '_' + item.conAppId + "\">" + sourceVal
                    + "</textarea></td><td class=\"padding-3 text-center work_break\"><textarea maxlength=\"50\" class=\"displayName_input\" id=\"fileName" + i + '_' + item.idApp + '_' + item.conAppId + "\">" + fileNameVal
                    + "</textarea></td></tr>"
            });
        } else {
            tableTbody = "<tr><td class=\"text-center\" colspan=\"5\">No data available</td></tr>"
        }
        tableDiv = "<div class=\"panel-val\" id=\"acc_panel" + ind + "\"><table class=\"tableCustom table-sm table table-bordered margin_top-15\">" + ""
            + "<thead><tr class=\"text-center\" ><td class=\"padding-3 width-170\">Validation name</td><td class=\"padding-3 template_width\">Template Name</td>" + ""
            + "<td class=\"padding-3 width-170\">Data Source</td><td class=\"padding-3 width-170\">Source</td><td class=\"padding-3 template_width\">File Name</td>" + ""
            + "</tr ></thead ><tbody>" + tableTbody + "</tbody></table ></div >";
        markup = "";
        if (beforeAcc) {
            for (let index = 0; index < indexOfCurrentAccBeforeList.length; index++) {
                $('#acc_btn' + indexOfCurrentAccBeforeList[index]).removeAttr('listner');
                acc_btn_body_before = $('#acc_btn' + indexOfCurrentAccBeforeList[index])[0].outerHTML;
                acc_panel_body_before = $('#acc_panel' + indexOfCurrentAccBeforeList[index])[0].outerHTML;
                beforeBody = beforeBody + acc_btn_body_before + acc_panel_body_before;
                $("#acc_btn" + indexOfCurrentAccBeforeList[index]).remove();
                $("#acc_panel" + indexOfCurrentAccBeforeList[index]).remove();
            }
        }
        if (afterAcc) {
            for (let index = 0; index < indexOfCurrentAccAfterList.length; index++) {
                $('#acc_btn' + indexOfCurrentAccAfterList[index]).removeAttr('listner');
                acc_btn_body_after = $('#acc_btn' + indexOfCurrentAccAfterList[index])[0].outerHTML;
                acc_panel_body_after = $('#acc_panel' + indexOfCurrentAccAfterList[index])[0].outerHTML;
                afterBody = afterBody + acc_btn_body_after + acc_panel_body_after;
                $("#acc_btn" + indexOfCurrentAccAfterList[index]).remove();
                $("#acc_panel" + indexOfCurrentAccAfterList[index]).remove();
            }
        }
        if (beforeAcc && afterAcc) {
            markup = beforeBody + buttonBody + tableDiv + afterBody;
        } else if (beforeAcc) {
            markup = beforeBody + buttonBody + tableDiv;
        } else if (afterAcc) {
            markup = buttonBody + tableDiv + afterBody;
        } else {
            markup = buttonBody + tableDiv;
        }
        accorBody = $('#connection-acc');
        accorBody.append(markup);
        var acc = document.getElementsByClassName("accordion-val");
        for (let i = 0; i < acc.length; i++) {
            if (!acc[i].hasAttribute('listner')) {
                $(acc[i]).attr('listner', true);
                acc[i].addEventListener("click", function () {
                    acc[i].classList.toggle("active-val");
                    var panel = acc[i].nextElementSibling;
                    if (panel.style.maxHeight) {
                        panel.style.maxHeight = null;
                    } else {
                        panel.style.maxHeight = panel.scrollHeight + "px";
                    }
                });
            }
        }
        // }
    }

    // To save all record
    $('#saveDashConfig').click(function () {
        if ($('#projectList').val() == null) {
            toastr.error('Please select project');
        } else {
            var passColorGrade = false;
            var passConnectionName = false;
            var passConnectionMapping = false;
            var actualListOfConnectionMap = [];
            var actualListConnection = [];
            var orderNoNotFound = false;
            if ($('#red-percent').val() != '' && $('#yellow-percent').val()) {
                var red_per = ($('#red-percent').val()).split('%')[0] != "" ? ($('#red-percent').val()).split('%')[0] : "1";
                var yel_per = ($('#yellow-percent').val()).split('%')[0];
                $(colorGradingList).each(function (i, item) {
                    if (i == 0) {
                        item.colorPercentage = red_per
                    } else {
                        item.colorPercentage = yel_per
                    }
                });
            }


            $(connectionForDashboardList).each(function (i, item) {
                item.displayName = $('#displayName' + i).val();
                item.displayOrder = $('#orderId' + i).val();
                if ($('#enable' + i)[0].checked && item.displayOrder != '') {
                    actualListConnection.push(item);
                }
                if ($('#enable' + i)[0].checked && item.displayOrder == '') {
                    orderNoNotFound = true;
                }
            });
            $(connectionValidationMapList).each(function (i, item) {
                $(item.response).each(function (ind, childItem) {
                    childItem.datasource = $('#dataSource' + ind + '_' + childItem.idApp + '_' + childItem.conAppId).val();
                    childItem.source = $('#source' + ind + '_' + childItem.idApp + '_' + childItem.conAppId).val();
                    childItem.fileName = $('#fileName' + ind + '_' + childItem.idApp + '_' + childItem.conAppId).val();
                    actualListOfConnectionMap.push(childItem);
                });
            });
            var domainId = $('#projectList').val().split('__')[0];
            var projectId = $('#projectList').val().split('__')[1];
            if (orderNoNotFound) {
                toastr.error('Please enter order before saving');
            }
            else if(!checkInputText()){ 
				alert('Vulnerability in submitted data, not allowed to submit!');
			}
            else {
                $.when(
                    $.ajax({
                        type: 'POST',
                        headers: { 'token':$("#token").val()},
                        url: 'dbconsole/updateConnectionsForDashboard?domainId=' + domainId + '&projectId=' + projectId,
                        datatype: 'json',
                        contentType: 'application/json',
                        data: JSON.stringify(actualListConnection),
                        success: function (res) {
                            passConnectionName = true;
                        },
                        error: function (error) {
                            passConnectionName = false;
                            console.log('error: ', error);
                        },
                        complete: function (complete) {
                        }
                    }),
                    $.ajax({
                        type: 'POST',
                        headers: { 'token':$("#token").val()},
                        url: 'dbconsole/updateConnectionValidationMap',
                        datatype: 'json',
                        contentType: 'application/json',
                        data: JSON.stringify(actualListOfConnectionMap),
                        success: function (response) {
                            passConnectionMapping = true;
                        },
                        error: function (error) {
                            passConnectionMapping = false;
                            console.log('error: ', error);
                        },
                        complete: function (complete) {
                        }
                    }),
                    $.ajax({
                        type: 'POST',
                        headers: { 'token':$("#token").val()},
                        url: 'dbconsole/updateColorGrading',
                        datatype: 'json',
                        contentType: 'application/json',
                        data: JSON.stringify(colorGradingList),
                        success: function (response) {
                            passColorGrade = true;
                        },
                        error: function (error) {
                            passColorGrade = false;
                            console.log('error: ', error);
                        },
                        complete: function (complete) {
                        }
                    })
                ).then(function () {
                    if (passConnectionName && passConnectionMapping && passColorGrade) {
                        toastr.success('Definition and Mapping saved succesfully.');
                    } else {
                        toastr.error('Unable to save definition and Mapping.');
                    }
                });
            }

        }

    });
</script>