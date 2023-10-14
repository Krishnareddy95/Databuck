
<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!--============= BEGIN CONTENT BODY============= -->


<!--*****************      BEGIN CONTENT **********************-->
<head>
<script type="text/javascript" src="./assets/global/plugins/jquery.min.js"></script>
<script type="text/javascript" src="./assets/global/plugins/jquery.validate.js"></script>
<script type="text/javascript" src="./assets/global/plugins/additional-methods.js"></script>
<jsp:include page="checkVulnerability.jsp" />	
<style>
.form-popup {
	bottom: 0;
	right: 15px;
	border: 3px solid #f1f1f1;
	background-color: while;
}

.header-box {
	padding-top: 15px;
	padding-bottom: 5px;
	padding-left: 10px;
}

.box {
	padding-top: 15px;
	padding-bottom: 15px;
	padding-left: 5px;
}

.popup-form-row {
	padding-bottom: 10px;
	padding-left: 10px;
	text-align: left;
}
/* Set a style for the submit/login button */
.pop-btn {
	background-color: #4CAF50;
	color: white;
	padding: 10px 20px;
	border: none;
	cursor: pointer;
	//margin-bottom: 10px;
	margin: auto;
	opacity: 0.8;
	
}

.popup-textAlign {
	text-align: left;
}

.popup-radio {
	position: relative;
	display: block;
	margin-top: 10px;
	margin-bottom: 10px;
}
</style>
</head>

<!--download report Form -->
<div class="cd-popup" id="downloadReportForm" role="alert">
	<div class="cd-popup-container">
		<form id="downloadCsvForm" class="form-popup " action="downloadCsvS3" method="post">
			<h4>Download CSV</h4>

			<input type="hidden" id="dr_idApp" name="dr_idApp"> 
			<input type="hidden" id="dr_tableName" name="dr_tableName"> 
			<input type="hidden" id="isDirect" name="isDirect" value="N"> 
			<input type="hidden" id="directCsvPath" name="directCsvPath"> 
			
			<input type="hidden" id="isCustomDateRangeDownload" name="isCustomDateRangeDownload" value="N"> 
			<input type="hidden" id="Date1" name="Date1" value=""> 
			<input type="hidden" id="Date2" name="Date2" value=""> 						
			
			<div class="header-box popup-textAlign">
				File Name: <input id="dr_tableNickName" name="dr_tableNickName" readonly>
			</div>
			
			<div id='legacyFormData' style='display: block'>
				<div class="box popup-textAlign">
					<div class="popup-radio">
						<label> <input type="radio" id="dr_fileSelected1"
							name="dr_fileSelected" value="LATEST_FILE" checked="checked">
							Download latest File
						</label> <label> <input type="radio" id="dr_fileSelected2"
							name="dr_fileSelected" value="CUSTOM_FILE"> Download
							custom File
						</label>
					</div>
				</div>
				<div id="customReportDataDiv" style="display: None;">
					<div class="row popup-form-row">
						<div class="col-md-6">
							<label class="form_control_1">Select Date</label>
							<div class="input-group input-medium date date-picker"
								data-date-format="yyyy-mm-dd">
								<input type="text" class="form-control" readonly id="reportDate"
									name="reportDate"> <span class="input-group-btn">
									<button class="btn default" type="button">
										<i class="fa fa-calendar"></i>
									</button>
								</span>
							</div>
						</div>
					</div>
					<div class="row popup-form-row">
						<div class="col-md-6">
							<label class="form_control_1">Enter Run </label>
							<div class="input-group input-medium">
								<input id="reportRun" class="form-control" name="reportRun"
									type="text">
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="box" style='padding-top: 30px; padding-bottom: 30px;'>
				<input id="legacyFormDataSubmit" class="pop-btn" type="button" value="Submit" style="display: block">
				<input id="enhancedFormDataSubmit" class="pop-btn" type="button" value="Download CSV Report" style="display: none">
			</div>
			<a onClick="validateHrefVulnerability(this)"  href="#0" class="cd-popup-close img-replace"></a>
		</form>
		<form>
				<input id="token" type="hidden" value="${sessionScope.csrfToken}" />
		</form>
	</div>
</div>


<!--download DQ Summary report Form -->
<div class="cd-popup" id="downloadSummaryPopUpDiv" role="alert">
	<div class="cd-popup-container">
		<form id="downloadDQSummaryForm" class="form-popup " action="downloadDQSummary" method="post">
			<h4>Download Summary</h4>

			<input type="hidden" id="sr_idApp" name="sr_idApp"> 
			
			<div class="box popup-textAlign">
				<div class="popup-radio">
					<label> <input type="radio" id="sr_summarySelected1"
						name="sr_summarySelected" value="LATEST_FILE" checked="checked">
						 Latest Summary
					</label> <label> <input type="radio" id="sr_summarySelected2"
						name="sr_summarySelected" value="CUSTOM_FILE">
						Custom Date Summary
					</label>
				</div>
			</div>
			<div id="customSummaryReportDataDiv" style="display: None;">
				<div class="row popup-form-row">
					<div class="col-md-6">
						<label class="form_control_1">Select Date</label>
						<div class="input-group input-medium date date-picker"
							data-date-format="yyyy-mm-dd">
							<input type="text" class="form-control" readonly id="summaryDate"
								name="summaryDate"> <span class="input-group-btn">
								<button class="btn default" type="button">
									<i class="fa fa-calendar"></i>
								</button>
							</span>
						</div>
					</div>
				</div>
				<div class="row popup-form-row">
					<div class="col-md-6">
						<label class="form_control_1">Enter Run </label>
						<div class="input-group input-medium">
							<input id="reportRun" class="form-control" name="summaryRun"
								type="text">
						</div>
					</div>
				</div>
			</div>
			
			<div class="box">
				<input id="sr_submit" class="pop-btn" type="button" value="Submit">
			</div>
			<a onClick="validateHrefVulnerability(this)"  href="#0" class="cd-popup-close img-replace"></a>
		</form>
		<form>
				<input id="token" type="hidden" value="${sessionScope.csrfToken}" />
		</form>
	</div>
</div>

<script>
	$(document).ready(
			function() {
				//close popup
				$('.cd-popup').on(
						'click',
						function(event) {
							if ($(event.target).is('.cd-popup-close')
									|| $(event.target).is('.cd-popup')) {
								event.preventDefault();
								$(this).removeClass('is-visible');
							}
						});
				//close popup when clicking the esc keyboard button
				$(document).keyup(function(event) {
					if (event.which == '27') {
						$('.cd-popup').removeClass('is-visible');
					}
				});
			});

	// To Show or hide the custom report form
	$(document).ready(function() {
		$('#dr_fileSelected1').click(function() {
			$("#customReportDataDiv").hide();
		});

		$('#dr_fileSelected2').click(function() {
			$("#customReportDataDiv").show();
		});		
		
		console.log('ManageDateRange.DateRange from download CSV logic added on value = ' + JSON.stringify(ManageDateRange.DateRange));

		if (ManageDateRange.DateRange.IsDownloadCsvDateRangeEnabled.toUpperCase() === 'Y') {
			$('#legacyFormData').css('display', 'none');
			$('#legacyFormDataSubmit').css('display', 'none');
			$('#enhancedFormDataSubmit').css('display', 'block');
		} else {
			$('#legacyFormData').css('display', 'block');
			$('#legacyFormDataSubmit').css('display', 'block');
			$('#enhancedFormDataSubmit').css('display', 'none');
		}	
		
		// Summary report selection
		$('#sr_summarySelected1').click(function() {
			$("#customSummaryReportDataDiv").hide();
		});

		$('#sr_summarySelected2').click(function() {
			$("#customSummaryReportDataDiv").show();
		});		
		

	});
	
	// Validate form
	$("#downloadCsvForm").validate({
		  focusInvalid: false,
		  ignore: [],
		  rules: {
			 reportDate: {
		      required: function(element) {
		        return $('#dr_fileSelected2').is(':checked')
		      }
			 },
	       reportRun: {
	          required: function(element) {
	          return $('#dr_fileSelected2').is(':checked')
	         }
		   }
		  }
		});
	
	// submit form
	var form = $( "#downloadCsvForm" );
	form.validate();
	$("#legacyFormDataSubmit" ).click(function( event ) {
		var oBauForm = $("#downloadCsvForm");
		
		if(form.valid()){
			$(oBauForm).attr('action', 'downloadCsvS3');			
		   form.submit();
	      $("#downloadReportForm").removeClass('is-visible');
		}
	});

	$("#enhancedFormDataSubmit" ).click(function( event ) {
		var oCustomForm = $("#downloadCsvForm");

		$('#isCustomDateRangeDownload').val('Y');
		$('#Date1').val(ManageDateRange.DateRange.StartDate);
		$('#Date2').val(ManageDateRange.DateRange.EndDate);

		$(oCustomForm).attr('action', 'DownloadCustomDateRangeReportAsCsv');
		
		oCustomForm.submit();
		$("#downloadReportForm").removeClass('is-visible');
	});
	
	
	// Validate summary form
	$("#downloadDQSummaryForm").validate({
		  focusInvalid: false,
		  ignore: [],
		  rules: {
			 summaryDate: {
		      required: function(element) {
		        return $('#sr_summarySelected2').is(':checked')
		      }
			 },
	         summaryRun: {
	          required: function(element) {
	          return $('#sr_summarySelected2').is(':checked')
	         }
		   }
		  }
		});
	
	// submit DQ summary form
	var dqSumryform = $( "#downloadDQSummaryForm" );
	dqSumryform.validate();
	$("#sr_submit" ).click(function( event ) {
		if(dqSumryform.valid()){
		  dqSumryform.submit();
	      $("#downloadSummaryPopUpDiv").removeClass('is-visible');
		}
	});
	
</script>
<script src="./assets/global/plugins/jquery.form.js"></script>
<script type="text/javascript">
	function downloadCsvReports(idApp, tableName, tableNickName) {
		//getting current Role id which is set in login process 
		var sRoleId = '${idRole}';
		console.log("RoleId: " + sRoleId);
		var form_data = { 
				idRole: sRoleId
		}
		//To check whether current user have access to download csv or not.
		$.ajax({
            url: './isDownloadCsvAllowed',
            type: 'POST',
            datatype: 'json',
            headers: { 'token':$("#token").val()},
            data: form_data,
            success: function(isAllowed) {
            	console.log("isAllowed: " + isAllowed);
            	var j_obj = $.parseJSON(isAllowed);
            	if (j_obj.hasOwnProperty('success')) {
                	showPopUp(idApp, tableName, tableNickName);
                } else {
                    toastr.info(j_obj["failed"]);
                }
            }
        });
	}
	
	//To get download csv popup.
	function showPopUp(idApp, tableName, tableNickName) { 
		console.log("tableName:" + tableName);
		console.log("tableNickName:" + tableNickName);
		console.log("idApp:" + idApp);

		// Resetting Date and Run fields
		$("#reportDate").val("");
		$("#reportRun").val("");
		
		if($('#dr_fileSelected2').is(':checked')){
			$("#customReportDataDiv").show();
		} else {
			$("#customReportDataDiv").hide();
		}

		// Displaying Div
		$('#downloadReportForm').addClass('is-visible');

		// Setting table and Id values
		$("#dr_tableName").val(tableName);
		$("#dr_tableNickName").val(tableNickName);
		$("#dr_idApp").val(idApp);
	}
	
	
	//To get download DQ summary popup.
	function downloadDQSummaryReport(idApp) { 
		console.log("idApp:" + idApp);

		// Resetting Date and Run fields
		$("#summaryDate").val("");
		$("#summaryRun").val("");
		
		if($('#sr_summarySelected2').is(':checked')){
			$("#customSummaryReportDataDiv").show();
		} else {
			$("#customSummaryReportDataDiv").hide();
		}

		// Displaying Div
		$('#downloadSummaryPopUpDiv').addClass('is-visible');

		// Setting table and Id values
		$("#sr_idApp").val(idApp);
	}
</script>
<script> 
        // wait for the DOM to be loaded 
        $(document).ready(function() { 
        	$('#downloadCsvForm').ajaxForm({
        		headers: {'token':$("#token").val()}
        	}); 
        }); 
 </script>
 <script> 
        // wait for the DOM to be loaded 
        $(document).ready(function() { 
        	$('#downloadDQSummaryForm').ajaxForm({
        		headers: {'token':$("#token").val()}
        	}); 
        }); 
 </script>
  <script type="text/javascript">
	function downloadCsvReportsDirect(idApp, reportDate, reportRun, tableNickName, directCsvPath) {
		//getting current Role id which is set in login process 
		var sRoleId = '${idRole}';
		console.log("RoleId: " + sRoleId);
		var form_data = { 
				idRole: sRoleId
		}
		//To check whether current user have access to download csv or not.
		$.ajax({
            url: './isDownloadCsvAllowed',
            type: 'POST',
            datatype: 'json',
            headers: { 'token':$("#token").val()},
            data: form_data,
            success: function(isAllowed) {
            	console.log("isAllowed: " + isAllowed);
            	var j_obj = $.parseJSON(isAllowed);
            	if (j_obj.hasOwnProperty('success')) {
            		showPopUpDirect(idApp, reportDate, reportRun, tableNickName, directCsvPath);
                } else {
                    toastr.info(j_obj["failed"]);
                }
            }
        });
	}
	
	
	//To get download csv popup.
	function showPopUpDirect(idApp, reportDate, reportRun, tableNickName, directCsvPath) { 
		console.log("tableNickName:" + tableNickName);
		console.log("idApp:" + idApp);
		
		$("#legacyFormData").hide();
		// Displaying Div
		$('#downloadReportForm').addClass('is-visible');

		// Setting table and Id values
		$("#reportDate").val(reportDate);
		$("#dr_tableName").val("Default");
		$("#dr_tableNickName").val(tableNickName);
		$("#dr_idApp").val(idApp);
		$("#reportRun").val(reportRun);
		$("#isDirect").val("Y");
		$("#directCsvPath").val(directCsvPath);
	}
	</script>
	

<!--********     BEGIN CONTENT ********************-->
