<%@page import="com.databuck.bean.ColumnProfile_DP"%>
<%@page import="com.databuck.bean.ColumnProfileDetails_DP"%>
<%@page import="com.databuck.bean.ColumnCombinationProfile_DP"%>
<%@page import="com.databuck.bean.NumericalProfile_DP"%>
<%@page import="com.databuck.bean.RowProfile_DP"%>
<%@page import="java.util.List"%>
<%@page import="com.databuck.service.RBACController"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<!-- jQuery -->
<script src="js/jquery-1.10.2.min.js"></script>
<script
	src="./assets/global/plugins/jquery.min.js"></script>

<script type="text/javascript"
	src="./assets/js/bootstrap-material-datetimepicker.js"></script>

<style>

.dataTables_scrollBody{
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
			<div class="col-md-12">
				<div class="portlet light bordered">

					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Summary of Template
								Profiling : ${appName} </span>
						</div>
						<div>
							<input type="hidden" id="idData" value="${idData}">
							<input type="hidden" id="profileTemplateData" value="${appName}">
							<button class="btn btn-primary" id="BtnPublish">Share
								Profile Results</button>
							<c:if test="${profileDataViewAccess=='Y'}" >
                                <button class="btn btn-primary" id="BtnDisplayData">Display Data</button>
                            </c:if>
						</div>

					</div>

					<div class="row">
						<div class="col-md-12" style="width: 100%;">
							<!-- BEGIN SAMPLE FORM PORTLET-->
							<div class="portlet light bordered init">
								<div class="portlet-title">
									<div class="caption font-red-sunglo">
										<span class="caption-subject bold ">Column Profile: (<a onClick="validateHrefVulnerability(this)" 
											href="profilingDownloadCsvS3?tableName=Column_Profile&idData=${idData}&tableNickName=ColumnProfile">Download
												csv</a>)
										</span>
									</div>
								</div>

								<div class="portlet-body">
									<table
										class="table table-striped table-bordered dataTable no-footer"
										id="columnProfile_tableId">
										<thead>
											<tr>
												<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Profiling Run Date</th>
												<th>Run</th>
												<th>Column_Name</th>
												<th>Data_Type</th>
												<th>Total_Record_Count</th>
												<th>Missing_Value</th>
												<th>Percentage_Missing</th>
												<th>Unique_Count</th>
												<th>Min_Length</th>
												<th>Max_Length</th>
												<th>Mean</th>
												<th>Std_Dev</th>
												<th>Min</th>
												<th>Max</th>
												<th>99_percentile</th>
												<th>75_percentile</th>
												<th>25_percentile</th>
												<th>1_percentile</th>
												<th>Default_Patterns</th>
											</tr>
										</thead>

										<tbody>
											<c:forEach var="columnProfileObj"
												items="${newColumnProfileList}">
												<tr style="background-color:#79d279">
													<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${columnProfileObj.execDate}</td>
													<td>${columnProfileObj.run}</td>
													<td>${columnProfileObj.columnName}<span class="columnlabel">New</span></td>
													<td>${columnProfileObj.dataType}</td>
													<td>${columnProfileObj.totalRecordCount}</td>
													<td>${columnProfileObj.missingValue}</td>
													<td>${columnProfileObj.percentageMissing}</td>
													<td>${columnProfileObj.uniqueCount}</td>
													<td>${columnProfileObj.minLength}</td>
													<td>${columnProfileObj.maxLength}</td>
													<td>${columnProfileObj.mean}</td>
													<td>${columnProfileObj.stdDev}</td>
													<td>${columnProfileObj.min}</td>
													<td>${columnProfileObj.max}</td>
													<td>${columnProfileObj.percentile_99}</td>
													<td>${columnProfileObj.percentile_75}</td>
													<td>${columnProfileObj.percentile_25}</td>
													<td>${columnProfileObj.percentile_1}</td>
													<td>${columnProfileObj.defaultPatterns}</td>
												</tr>
											</c:forEach>
											<c:forEach var="columnProfileObj"
												items="${missingColumnProfileList}">
												<tr style="background-color:#ff8080">
													<td>${columnProfileObj.execDate}</td>
													<td>${columnProfileObj.run}</td>
													<td>${columnProfileObj.columnName}<span class="columnlabel">Deleted</span></td>
													<td>${columnProfileObj.dataType}</td>
													<td>${columnProfileObj.totalRecordCount}</td>
													<td>${columnProfileObj.missingValue}</td>
													<td>${columnProfileObj.percentageMissing}</td>
													<td>${columnProfileObj.uniqueCount}</td>
													<td>${columnProfileObj.minLength}</td>
													<td>${columnProfileObj.maxLength}</td>
													<td>${columnProfileObj.mean}</td>
													<td>${columnProfileObj.stdDev}</td>
													<td>${columnProfileObj.min}</td>
													<td>${columnProfileObj.max}</td>
													<td>${columnProfileObj.percentile_99}</td>
													<td>${columnProfileObj.percentile_75}</td>
													<td>${columnProfileObj.percentile_25}</td>
													<td>${columnProfileObj.percentile_1}</td>
													<td>${columnProfileObj.defaultPatterns}</td>
												</tr>
											</c:forEach>
											<c:forEach var="columnProfileObj"
												items="${columnProfileList}">
												<tr>
													<td>${columnProfileObj.execDate}</td>
													<td>${columnProfileObj.run}</td>
													<td>${columnProfileObj.columnName}</td>
													<td>${columnProfileObj.dataType}</td>
													<td>${columnProfileObj.totalRecordCount}</td>
													<td>${columnProfileObj.missingValue}</td>
													<td>${columnProfileObj.percentageMissing}</td>
													<td>${columnProfileObj.uniqueCount}</td>
													<td>${columnProfileObj.minLength}</td>
													<td>${columnProfileObj.maxLength}</td>
													<td>${columnProfileObj.mean}</td>
													<td>${columnProfileObj.stdDev}</td>
													<td>${columnProfileObj.min}</td>
													<td>${columnProfileObj.max}</td>
													<td>${columnProfileObj.percentile_99}</td>
													<td>${columnProfileObj.percentile_75}</td>
													<td>${columnProfileObj.percentile_25}</td>
													<td>${columnProfileObj.percentile_1}</td>
													<td>${columnProfileObj.defaultPatterns}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="col-md-12" style="width: 100%;">
							<!-- BEGIN SAMPLE FORM PORTLET-->
							<div class="portlet light bordered init">
								<div class="portlet-title">
									<div class="caption font-red-sunglo">
										<span class="caption-subject bold ">Column Profile
											Detail: (<a onClick="validateHrefVulnerability(this)" 
											href="profilingDownloadCsvS3?tableName=Column_Profile_Details&idData=${idData}&tableNickName=ColumnProfileDetail">Download
												csv</a>)
										</span>
									</div>
								</div>
								<div class="portlet-body">
									<table
										class="table table-striped table-bordered dataTable no-footer"
										id="columnProfileDetail_tableId">
										<thead>
											<tr>
												<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Profiling Run Date</th>
												<th>Run</th>
												<th>Column_Name</th>
												<th>Column_Value</th>
												<th>Count</th>
												<th>Percentage</th>
											</tr>
										</thead>

										<tbody>
											<c:forEach var="columnProfileDetailObj"
												items="${columnProfileDetailsList}">
												<tr>
													<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${columnProfileDetailObj.execDate}</td>
													<td>${columnProfileDetailObj.run}</td>
													<td>${columnProfileDetailObj.columnName}</td>
													<td>${columnProfileDetailObj.columnValue}</td>
													<td>${columnProfileDetailObj.count}</td>
													<td>${columnProfileDetailObj.percentage}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="col-md-12" style="width: 100%;">
							<!-- BEGIN SAMPLE FORM PORTLET-->
							<div class="portlet light bordered init">
								<div class="portlet-title">
									<div class="caption font-red-sunglo">
										<span class="caption-subject bold "> Column Combination
											Profile: (<a onClick="validateHrefVulnerability(this)" 
											href="profilingDownloadCsvS3?tableName=Column_Combination_Profile&idData=${idData}&tableNickName=ColumnCombinationProfile">Download
												csv</a>)
										</span>
									</div>
								</div>
								<div class="portlet-body">
									<table
										class="table table-striped table-bordered dataTable no-footer"
										id="columnCombProfileDetail_tableId">
										<thead>
											<tr>
												<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Profiling Run Date</th>
												<th>Run</th>
												<th>Column_Group_Name</th>
												<th>Column_Group_Value</th>
												<th>Count</th>
												<th>Percentage</th>
											</tr>
										</thead>

										<tbody>
											<c:forEach var="columnCombProfileObj"
												items="${columnCombinationProfileList}">
												<tr>
													<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${columnCombProfileObj.execDate}</td>
													<td>${columnCombProfileObj.run}</td>
													<td>${columnCombProfileObj.column_Group_Name}</td>
													<td>${columnCombProfileObj.column_Group_Value}</td>
													<td>${columnCombProfileObj.count}</td>
													<td>${columnCombProfileObj.percentage}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
					<!-- Commenting Row Profile For Now, will be enhanced and uncommented in future-->

					<% /*
					<div class="row">
						<div class="col-md-12" style="width: 100%;">
							<!-- BEGIN SAMPLE FORM PORTLET-->
							<div class="portlet light bordered init">
								<div class="portlet-title">
									<div class="caption font-red-sunglo">
										<span class="caption-subject bold ">Row Profile: (<a onClick="validateHrefVulnerability(this)" 
											href="profilingDownloadCsvS3?tableName=Row_Profile&idData=${idData}&tableNickName=RowProfile">Download
												csv</a>)
										</span>
									</div>
								</div>
								<div class="portlet-body">
									<table
										class="table table-striped table-bordered dataTable no-footer"
										id="rowProfile_tableId">
										<thead>
											<tr>
												<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Profiling Run Date</th>
												<th>Run</th>
												<th>Number_of_Columns_with_NULL</th>
												<th>Number_of_Records</th>
												<th>Percentage_Missing</th>
											</tr>
										</thead>

										<tbody>
											<c:forEach var="rowProfileObj" items="${rowProfileList}">
												<tr>
													<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${rowProfileObj.execDate}</td>
													<td>${rowProfileObj.run}</td>
													<td>${rowProfileObj.number_of_Columns_with_NULL}</td>
													<td>${rowProfileObj.number_of_Records}</td>
													<td>${rowProfileObj.percentageMissing}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				
				  */	%>			

					<div class="row">
						<div class="col-md-12" style="width: 100%;">
							<!-- BEGIN SAMPLE FORM PORTLET-->
							<div class="portlet light bordered init">
								<div class="portlet-title">
									<div class="caption font-red-sunglo">
										<span class="caption-subject bold ">Numerical Profile:
											(<a onClick="validateHrefVulnerability(this)" 
											href="profilingDownloadCsvS3?tableName=Numerical_Profile&idData=${idData}&tableNickName=NumericalProfile">Download
												csv</a>)
										</span>
									</div>
								</div>
								<div class="portlet-body">
									<table
										class="table table-striped table-bordered dataTable no-footer"
										id="numericProfile_tableId">
										<thead>
											<tr>
												<th style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">Profiling Run Date</th>
												<th>Run</th>
												<th>Column_Name_1</th>
												<th>Column_Name_2</th>
												<th>Correlation(>0.25)</th>
											</tr>
										</thead>

										<tbody>
											<c:forEach var="numericProfileObj"
												items="${numericProfileList}">
												<tr>
													<td style="max-width: 75px !important;width: 75px !important;min-width: 75px !important">${numericProfileObj.execDate}</td>
													<td>${numericProfileObj.run}</td>
													<td>${numericProfileObj.columnName}</td>
													<td>${numericProfileObj.columnName1}</td>
													<td>${numericProfileObj.correlation}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>

				</div>
			</div>
		</div>
	</div>

	<div>
		<div id='form-body-parent' style='display: none; min-width: 550px;'>
			<fieldset id='form-body-profile-entry' class="fieldset-row" >


				<label for="emailToPublish">Email id to Publish</label> <input
					type="email" id="emailToPublish"> <br>

				<textarea id='form-databuckLink-msg' readonly data-msgstate="hint" style="width:560px" ></textarea>

			</fieldset>
		</div>
	</div>

	<div>
        <div id='view-data-body'>
               <table class="table table-striped table-bordered dataTable no-footer" id="viewDataTableId"> </table>
        </div>
    </div>

</div>
<script src="./assets/global/plugins/jquery.min.js"
	type="text/javascript">
	
</script>
<script
	src="./assets/global/plugins/jquery.dataTables.min.js"
	type="text/javascript"></script>

<head>
<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

<script>
var ProfileViewDataEntry = {
		DialogBody: null,
		MsgTextArea: null,
		isEmailValid : false,
		EmailDataToSend : {},
		setup: function() {
			ProfileViewDataEntry.MsgTextArea = $('#form-databuckLink-msg')[0];
			ProfileViewDataEntry.DialogBody = $('#form-body-profile-entry')[0];
			
			$('#emailToPublish').on('change', ProfileViewDataEntry.onChangeData);
		},
		
		onChangeData: function(oEvent) {
			var sFieldName = oEvent.target.id, sFieldValue = $('#' + sFieldName).val().trim(), lValidData = true, sErrorMsg = '';
			var sEmail= $('#emailToPublish').val().trim();
			var lIsMainDataBlank = ( (sEmail.length < 1) ) ? true : false;
			
			if (sFieldName === 'emailToPublish') {
				lValidData = /^(([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)(\s*(;|,)\s*|\s*$))+/.test(sFieldValue);
				sErrorMsg = (lValidData) ? "" : "Email id is not proper.";
			}
			if (sErrorMsg.length > 0) {			
				ProfileViewDataEntry.isEmailValid = false;
			} else {
				ProfileViewDataEntry.isEmailValid = true;
			}
			
			if (lIsMainDataBlank) { ProfileViewDataEntry.isEmailValid = false; }
		},
		
		sendMail: function(sMessage) {
			
			var oEmailDataToSend = ProfileViewDataEntry.EmailDataToSend;
			
			oEmailDataToSend.Email = $('#emailToPublish').val();
			PageCtrl.debug.log('oEmailDataToSend.Email', oEmailDataToSend.Email);
			
			oEmailDataToSend.Subject = "DataBuck Link to View Profile Results for Template '" + ${idData} + "'";
			PageCtrl.debug.log('oEmailDataToSend.Subject', oEmailDataToSend.Subject);
			
			oEmailDataToSend.Message = sMessage;
			PageCtrl.debug.log('oEmailDataToSend.Message', sMessage);
			
			PageCtrl.debug.log('Is email valid', ProfileViewDataEntry.isEmailValid);
			
			if (ProfileViewDataEntry.isEmailValid) {
				
				JwfAjaxWrapper({
					WaitMsg: 'Sending Mail',
					Url: 'sendProfileResultsMail',
					Headers:$("#token").val(),
					Data: { EmailDataToSend: JSON.stringify(oEmailDataToSend) },
					CallBackFunction: function(oResponse) {				
						if (oResponse.Result) {
							Modal.confirmDialog(3, 'Mail sent', oResponse.Msg, ['Ok'] );
						} else {					
							Modal.confirmDialog(3, 'Send Failed', oResponse.Msg, ['Ok'] );
						}
					}				
				});
			} else {
				Modal.confirmDialog(4, 'Invalid email', 'Please enter a valid email', ['Ok'] );
			}
			
		},
		
		promotePublishDialog: function() {
			
			var sModeTitle = "Publish Profile Results for Template '" + ${idData} + "'";
			PageCtrl.debug.log('Title', sModeTitle);
			
			var sDatabuckLink = window.location.href ;
			var templateInfo = "To view the profiling results for Template '" + ${idData} + " - " + $('#profileTemplateData').val() +  "' visit or open below DataBuck link:\n";
			ProfileViewDataEntry.MsgTextArea.value = templateInfo + sDatabuckLink;
			var sMessage = templateInfo + sDatabuckLink;
			
			Modal.customDialog(1, sModeTitle,
					{ 'BodyDomObj': ProfileViewDataEntry.DialogBody,'HeaderHtml': null },
					['Publish', 'Cancel'], { onDialogClose: cbPromoteDialog, onDialogLoad: cbPromoteDialog });

			return;
			
			function	cbPromoteDialog(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						PageCtrl.debug.log('on Dialog load','ok');
						break;
						
					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						if (sBtnSelected === 'Publish') { ProfileViewDataEntry.sendMail(sMessage); }
						break;
				}
			}
		}
		
	}

	$('#BtnPublish').on('click', function() {
		ProfileViewDataEntry.promotePublishDialog();
	});

	$(document).ready(function() {
		initializeJwfSpaInfra(); // Load JWF framework module on this page
		ProfileViewDataEntry.setup();
	});
</script>

<script>
var ViewData = {
		DialogBody: null,
		ViewPageData: {},
		setup: function() {
			ViewData.DialogBody = $('#view-data-body')[0];
		},

		promoteViewDataDialog: function() {

			var sModeTitle = "Data Results for Template '" + ${idData} + "'";
			PageCtrl.debug.log('Title', sModeTitle);

			Modal.customDialog(1, sModeTitle,
					{'BodyDomObj': ViewData.DialogBody,'HeaderHtml': null },
					['Cancel'], { onDialogClose: cbDataDialog, onDialogLoad: cbDataDialog });
			return;

			function cbDataDialog(oEvent) {
				var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;
				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
						PageCtrl.debug.log('on Dialog load','ok');
						break;
					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
                        break;
				}
			}
		},

	}

	$('#BtnDisplayData').on('click', function() {
        var sViewTable = $('#viewDataTableId');
        JwfAjaxWrapper({
            WaitMsg: 'Loading data',
            Url: 'getProfileDataForTemplate',
            Data: {"idData":  ${idData} },
            Headers:$("#token").val(),
            CallBackFunction: function(oResponse) {
                var columns = [];
                for (var i = 0; i < oResponse.result.sourceData.header.length; i++) {
                    var headerVal = oResponse.result.sourceData.header[i];
                    var my_item = {};
                    my_item.data = headerVal;
                    my_item.title = headerVal;
                    columns.push(my_item);
                }
                var data = oResponse.result.sourceData.data;
                $(sViewTable).DataTable({
                    "data" : data,
                    "columns": columns,
                    scrollX: true,
                    scrollCollapse: true,
                    scrollY: '400px',
                    autoWidth: true,
                    order: [[ 0, 'asc' ]],
                    destroy: true,
                    orderClasses: false
                })
                ViewData.promoteViewDataDialog();
            }
        });
	});

    $(document).ready(function() {
		ViewData.setup();
	});
</script>

<script>
	$(document).ready(function() {
		var table;
		table = $('#columnProfile_tableId').dataTable({
			order : [ [ 0, "desc" ] ],
			"scrollX" : true,
			'columnDefs': [
		        {
		           'targets': 18,
		           'render': function(data, type, full, meta){
		              return data.replace("val:",'').replace(/val:/g,"<br>");
		           }
		        }
		     ],
			"aoColumns": [
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "200" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "100" },
	            { "sWidth": "200" }
	        ],
	        fixedColumns: true
		});
		table = $('#columnProfileDetail_tableId').dataTable({
			order : [ [ 0, "desc" ] ]
		});
		table = $('#columnCombProfileDetail_tableId').dataTable({
			order : [ [ 0, "desc" ] ]
		});
		table = $('#rowProfile_tableId').dataTable({
			order : [ [ 0, "desc" ] ]
		});
		table = $('#numericProfile_tableId').dataTable({
			order : [ [ 0, "desc" ] ]
		});
	});
</script>

<jsp:include page="footer.jsp" />