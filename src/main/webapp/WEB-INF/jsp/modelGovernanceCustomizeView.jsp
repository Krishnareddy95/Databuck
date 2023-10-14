
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
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
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Create Validation
								Check </span>
						</div>
					</div>
					<div class="portlet-body form">
						<input type="hidden" id="idApp" name="idApp" value="${idApp}" />
						<input type="hidden" id="idData" name="idData" value="${idData}" />
						<input type="hidden" id="apptype" name="apptype"
							value="${apptype}" /> <input type="hidden" id="applicationName"
							name="applicationName" value="${applicationName}" /> <input
							type="hidden" id="description" name="description"
							value="${description}" /> <input type="hidden"
							id="modelGovernanceType" value="${modelGovernanceType}" /> <input
							type="hidden" id="modelIdColStatus" value="${modelIdCol}" /> <input
							type="hidden" id="decileColStatus" value="${decileCol}" />   <input
							type="hidden" id="incrementalMatching"
							value="${incrementalMatching}" /> <input type="hidden"
							id="buildHistoricFingerPrint" value="${buildHistoricFingerPrint}" />
						<input type="hidden" id="historicStartDate"
							value="${historicStartDate}" /> <input type="hidden"
							id="historicEndDate" value="${historicEndDate}" /> <input
							type="hidden" id="historicDateFormat"
							value="${historicDateFormat}" /> <input type="hidden"
							id="incrementalMatching" value="${incrementalMatching}" /> <input
							type="hidden" value="${month}" id="month" /> <input
							type="hidden" value="${dayOfWeek}" id="dayOfWeek" /> <input
							type="hidden" value="${hourOfDay}" id="hourOfDay" /> <input
							type="hidden" value="${dayOfMonth}" id="dayOfMonth" /> <input
							type="hidden" value="${None}" id="noneStatus" /> <input
							type="hidden" value="${updateFrequency}" id="updateFrequency" />
						<input type="hidden" value="${frequencyDays}" id="frequencyDays" />
						<!--  <form role="form" method="post" action="<?php //echo site_url('User/uploadCsv'); ?>">  -->
						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="appnameid" name="dataset" value="${name }" readonly /> <label
											for="form_control_1">Validation Check Name *</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="descriptionid"
											name="description" placeholder="Enter your description"
											value="${description }" readonly /> <label
											for="form_control">Description</label>
										<!-- <span class="help-block">Short text about Data Set</span> -->
									</div>
								</div>
							</div>

							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="descriptionid"
											name="description" value="${apptype }" readonly /> <label
											for="form_control">Validation Check Type</label>
										<!-- <span class="help-block">Short text about Data Set</span> -->
									</div>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="descriptionid"
											name="description" value="${applicationName}" readonly>
										<label for="form_control">Data Template</label>
										<!-- <span class="help-block">Short text about Data Set</span> -->
									</div>
								</div>
							</div>

							<br>
						</div>
						<div class="row">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<select class="form-control" id="apptypeid" name="apptypeid" disabled="disabled">
										<option value="Decile Equality">Decile Equality</option>
										<option value="Decile Consistency">Decile Consistency</option>
											<option value="Score Consistency">Score Consistency</option>
									</select> <label for="form_control_1">Choose Model Governance*</label>
								</div>
							</div>
						</div>
						<br />
						<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="modelIdCol" name="modelIdCol">
											<option value="-1">Select Data Column</option>
											<c:forEach var="listDataDefinitionColumnNames"
												items="${listDataDefinitionColumnNames}">
												<option value="${listDataDefinitionColumnNames}">${listDataDefinitionColumnNames}</option>
											</c:forEach>
										</select> <label for="columnid">Choose Model Id Col*</label><br>
									</div>
								</div>
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="decileCol" name="decileCol">
											<option value="-1">Select Data Column</option>
											<c:forEach var="listDataDefinitionColumnNames"
												items="${listDataDefinitionColumnNames}">
												<option value="${listDataDefinitionColumnNames}">${listDataDefinitionColumnNames}</option>
											</c:forEach>
										</select> <label for="columnid">Choose Decile Col*</label><br>
									</div>
								</div>
							</div>
						<div class="" id="decileEqualityConsistency">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="expectedPercentage" name="expectedPercentage"
											value="${expectedPercentage}" /> <label for="form_control_1"
											id="thresholdid"> Expected Percentage</label>
									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture" id="decilecoldivid">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="thresholdPercentage" name="thresholdPercentage"
											value="${thresholdPercentage}" /><label for="form_control_1">
											Threshold Percentage</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<div class="row">
								<div class="col-md-12">
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="buildHistoricId"
														name="buildHistoricId" class="md-check" value="Y">
													<label for="buildHistoricId"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<!-- <input type="text" class="form-control"
													id="groupEqualityText" name="groupEqualityText"> -->
											<label for="buildHistoricId">Select Date Range</label>
										</div>
									</div>
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="incrementalTypeId"
														name="incrementalTypeId" class="md-check" value="Y">
													<label for="incrementalTypeId"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<!-- <input type="text" class="form-control"
													id="groupEqualityText" name="groupEqualityText"> -->
											<label for="incrementalTypeId">Incremental Type</label>
										</div>
									</div>
								</div>
							</div>
							<br />
							<div class="row hidden" id="dateDivId">
								<div class="col-md-4">
									<label class="form_control_1">Start Date</label>
									<div class="input-group input-medium date date-picker"
										data-date-format="yyyy-mm-dd">
										<input type="text" class="form-control" readonly
											id="startdateid" name="startdateid"
											value="${historicStartDate}"> <span
											class="input-group-btn">
											<button class="btn default" type="button">
												<i class="fa fa-calendar"></i>
											</button>
										</span>
									</div>
								</div>
								<div class="col-md-4">
									<label class="form_control_1">End Date</label>
									<div class="input-group input-medium date date-picker"
										data-date-format="yyyy-mm-dd">
										<input type="text" class="form-control" readonly
											id="enddateid" name="enddateid" value="${historicEndDate}">
										<span class="input-group-btn">
											<button class="btn default" type="button">
												<i class="fa fa-calendar"></i>
											</button>
										</span>
									</div>
								</div>
								<div class="col-md-4">
									<label class="form_control_1">Date Format</label> <input
										type="text" class="form-control" id="dateformatid"
										name="dateformatid" value="${historicDateFormat}">
								</div>
							</div>
							<br />
							<div class="hidden">
							 &nbsp&nbsp&nbsp Data Cyclicality<br />
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-6">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="none" name="check_id"
														class="md-check" value="None"> <label for="none">
														None<span></span> <span class="check"></span> <span
														class="box"></span>
													</label>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div id="noneid">
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-4">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="chkmonth" name="check_id"
															class="md-check" value="month"> <label
															for="chkmonth"> Month<span></span> <span
															class="check"></span> <span class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-4">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="chkDayOfWeek" name="check_id"
															class="md-check" value="dayOfWeek"> <label
															for="chkDayOfWeek"> Day of Week<span></span> <span
															class="check"></span> <span class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>

									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-4">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="chkHourOfDay" name="check_id"
															class="md-check" value="hourOfDay"> <label
															for="chkHourOfDay"> Hour of Day<span></span> <span
															class="check"></span> <span class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="col-md-4">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="chkDayOfMonth" name="check_id"
															class="md-check" value="dayOfMonth"> <label
															for="chkDayOfMonth"> Day of Month<span></span> <span
															class="check"></span> <span class="box"></span>
														</label>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>

						<br />
						<div class="row">
							<div class="col-md-12">
								<div class="col-md-6">
									<div class="form-group form-md-line-input">
										<select name="frequencyid" id="frequencyid"
											class="form-control selectfrequency">
											<option value="Never">Never</option>
											<option value="Daily">Daily</option>
											<option value="EveryDay">Every 'x' Days</option>
										</select> <br /> <label for="form_control_1">Update Frequency</label>
									</div>
								</div>
								<div class="col-md-6 hidden" id="EveryDaydiv">
									<div class="form-group form-md-checkboxes">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="EveryDay"
												name="EveryDay" value="${frequencyDays}"> <label
												for="EveryDay">Every 'x' Days</label>
										</div>
									</div>
								</div>
							</div>
						</div>
						</div>
						<br />
						<div class="form-actions noborder align-center tab-one-save">
							<button type="submit" id="matchkeycreateid" class="btn blue">Save</button>
						</div>
						</div>
						
						<div class="hidden" id="scoreConsistencyDiv">
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-6">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold "> LEFT SOURCE </span>
										</div>
									</div>
									<div class="col-md-6">
										<div class="caption font-red-sunglo">
											<span class="caption-subject bold "> RIGHT SOURCE </span>
										</div>
									</div>
								</div>
							</div>
							<br />
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-3">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="leftsourceslicestart" value="${leftSourceSliceStart}">
											<label for="leftsourceslicestart">SLICE START </label>
										</div>
									</div>
									<div class="col-md-3">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="leftsourcesliceend" value="${leftSourceSliceEnd}">
											<label for="leftsourcesliceend">SLICE END </label>
										</div>
									</div>
									<div class="col-md-3">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="rightsourceslicestart" value="${rightSourceSliceStart}">
											<label for="rightsourceslicestart">SLICE START </label>
										</div>
									</div>
									<div class="col-md-3">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="rightsourcesliceend" value="${rightSourceSliceEnd}">
											<label for="rightsourcesliceend">SLICE END </label>
										</div>
									</div>
								</div>
							</div><br/>
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-6 col-capture" >
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="scthresholdid"
												name="scthresholdid" value="${threshold}"> <label
												for="form_control">Threshold</label>
										</div>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="sourcedateformatid"
												name="sourcedateformatid" value="${sourcedateformat}"> <label
												for="form_control">Source Date Format</label>
										</div>
									</div>
								</div>
							</div><br/>
						<div class="row">
								<div class="col-md-12">
								<div class="col-md-12">
									<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="matchingexpression" value="${matchingExpression}">
											<label for="matchingexpression">Matching Expression </label>
										</div>
										</div></div></div><br/>
							<div class="row">
								<div class="col-md-12">
								<div class="col-md-12">
									<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="measurementexpression" value="${measurementExpression}">
											<label for="measurementexpression">Measurement Expression </label>
										</div>
										</div></div></div>
										<div class="form-actions noborder align-center tab-one-save">
								<button type="submit" id="scoreconsistencycreateid" class="btn blue">Save</button>
							</div>
						</div>
						
					</div>
					<br />
					<br />
				</div>
			</div>
		</div>
	</div>
</div>


<!-- END EXAMPLE TABLE PORTLET-->

<jsp:include page="footer.jsp" />
<script>
$(document).ready(function(){
	var apptype = $("#modelGovernanceType").val();
	//alert(apptype);
	//var modelGovernanceType = $("#apptypeid").val();
	$("#apptypeid").val(apptype);
	if (apptype == "Decile Equality") {
		$('#decilecoldivid').removeClass('hidden').show();
		$('#decileEqualityConsistency').removeClass('hidden').show();
		//$('#thresholdPercentage').val("");
		$('#thresholdid').text("Expected Percentage");
		$('#scoreConsistencyDiv').addClass('hidden');
	} else if (apptype == "Decile Consistency") {
		//$('#decilecoldivid').removeClass('hidden').show();
		$('#decileEqualityConsistency').removeClass('hidden').show();
		$('#decilecoldivid').addClass('hidden');
		$('#scoreConsistencyDiv').addClass('hidden');
		$('#thresholdid').text("Threshold");
	} else {
		$('#scoreConsistencyDiv').removeClass('hidden').show();
		$('#decileEqualityConsistency').addClass('hidden');
	}
	if (apptype=="Decile Equality" || apptype=="Decile Consistency"){
		
		var modelIdCol = $("#modelIdColStatus").val();
		var decileCol = $("#decileColStatus").val();
		//alert(modelGovernanceType);
		
	
		$("#modelIdCol").val(modelIdCol);
		$("#decileCol").val(decileCol);
	

		var incrementalMatching=$("#incrementalMatching").val();
		if(incrementalMatching=="Y"){
			$("#incrementalTypeId").attr("checked",true);
		}else{
			$("#incrementalTypeId").attr("checked",false);
		}
		var buildHistoricFingerPrint=$("#buildHistoricFingerPrint").val();
		if(buildHistoricFingerPrint=="Y"){
			$("#buildHistoricId").attr("checked",true);
			$('#dateDivId').removeClass('hidden').show();
		}else{
			$("#buildHistoricId").attr("checked",false);
			$('#dateDivId').addClass("hidden");
		}
		
		var noneStatus = $("#noneStatus").val();
		var month = $("#month").val();
		var dayOfWeek = $("#dayOfWeek").val();
		var hourOfDay = $("#hourOfDay").val();
		var dayOfMonth = $("#dayOfMonth").val();
		var frequencyDays = $("#frequencyDays").val();
		var updateFrequency = $("#updateFrequency").val();
		//alert(updateFrequency);
		if(updateFrequency=="Daily"){
			$(".selectfrequency").val(updateFrequency);
		}else if(updateFrequency=="Never"){
			$(".selectfrequency").val(updateFrequency);
		}else{
			$(".selectfrequency").val(updateFrequency);
			$('#EveryDaydiv').removeClass('hidden').show();
		}
		
		if(noneStatus== "None"){
			//alert(noneStatus);
			$("#none").attr("checked",true);
			$(".divmonth").addClass("hidden");
			$(".divHourOfDay").addClass("hidden");
			$(".divDayOfMonth").addClass("hidden");
			$(".divDayOfWeek").addClass("hidden");
			$('#noneid').addClass('hidden');
		}else{
		}
		
		if(month!= ""){
			$("#chkmonth").attr("checked",true);
			$(".divmonth").removeClass("hidden");
			$(".month").val(month);
		}else{
			$("#chkmonth").attr("checked",false);
			$(".divmonth").addClass("hidden");
		}
		
		if(dayOfWeek!=""){
			$("#chkDayOfWeek").attr("checked",true);
			$(".divDayOfWeek").removeClass("hidden");
			$(".dayOfWeek").val(dayOfWeek);
		}else{
			$("#chkDayOfWeek").attr("checked",false);
			$(".divDayOfWeek").addClass("hidden");
		}
		
		if(hourOfDay!=""){
			console.log(hourOfDay);
			$("#chkHourOfDay").attr("checked",true);
			$(".divHourOfDay").removeClass("hidden");
			$(".hourofDay").val(hourOfDay);
		}else{
			$("#chkHourOfDay").attr("checked",false);
			$(".divHourOfDay").addClass("hidden");
		}
		
		if(dayOfMonth!=""){
			console.log(dayOfMonth);
			$("#chkDayOfMonth").attr("checked",true);
			$(".divDayOfMonth").removeClass("hidden");
			$(".dayOfMonth").val(dayOfMonth);
		}else{
			$("#chkDayOfMonth").attr("checked",false);
			$(".divDayOfMonth").addClass("hidden");
		}
		
	}else if(apptype=="Score Consistency"){
		var modelIdCol = $("#modelIdColStatus").val();
		var decileCol = $("#decileColStatus").val();
		//alert(modelGovernanceType);
		
	
		$("#modelIdCol").val(modelIdCol);
		$("#decileCol").val(decileCol);
	}
	
		
		
	});



$('#frequencyid').change(function() {
	$this = $(this);
	var selectedVal = $this.val();
	if (selectedVal == 'EveryDay') {
		$('#EveryDaydiv').removeClass('hidden').show();
	} else {
		$('#EveryDaydiv').addClass('hidden');
	}
});
$("#buildHistoricId").click(function() {
	if ($('#buildHistoricId').is(':checked')) {
		$('#dateDivId').removeClass('hidden').show();
	} else {
		$('#dateDivId').addClass("hidden");
	}
});
$("#none").click(function() {
	if ($('#none').is(':checked')) {
		$('#month').prop("checked", false);
		$('#dow').prop("checked", false);
		$('#hod').prop("checked", false);
		$('#dom').prop("checked", false);
		$('#noneid').addClass('hidden');
	} else {
		$('#noneid').removeClass('hidden').show();
	}
});

$('#apptypeid').change(function() {
	var apptype = $('#apptypeid').val();
	if (apptype == "Decile Equality") {
		$('#decilecoldivid').removeClass('hidden').show();
		$('#decileEqualityConsistency').removeClass('hidden').show();
		//$('#thresholdPercentage').val("");
		$('#thresholdid').text("Expected Percentage");
		$('#scoreConsistencyDiv').addClass('hidden');
	} else if (apptype == "Decile Consistency") {
		//$('#decilecoldivid').removeClass('hidden').show();
		$('#decileEqualityConsistency').removeClass('hidden').show();
		$('#decilecoldivid').addClass('hidden');
		$('#scoreConsistencyDiv').addClass('hidden');
		$('#thresholdid').text("Threshold");
	} else {
		$('#scoreConsistencyDiv').removeClass('hidden').show();
		$('#decileEqualityConsistency').addClass('hidden');
	}
});

	$("#matchkeycreateid").click(function() {
		var no_error = 1;
		$('.input_error').remove();
		
		if(!checkInputText()){ 
			no_error = 0;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}

		 if($("#modelIdCol").val() == "-1"){
		    $('<span class="input_error" style="font-size:12px;color:red">Please Choose Model Id Col</span>').insertAfter($('#modelIdCol'));
		    no_error = 0;
		} 
		 if($("#decileCol").val() == "-1"){
			    $('<span class="input_error" style="font-size:12px;color:red">Please Choose Decile Col</span>').insertAfter($('#decileCol'));
			    no_error = 0;
			} 
		var expectedPercentage= $("#expectedPercentage").val();
		var thresholdPercentage = $("#thresholdPercentage").val();
		if(expectedPercentage==""){
			expectedPercentage=0;
		}
		if(thresholdPercentage==""){
			thresholdPercentage=0;
		}
		
		var buildHistoric;
		if($("#buildHistoricId").is(':checked')){
			buildHistoric="Y";
		}else{
			buildHistoric="N";  // unchecked
		}
		
		var incrementalType;
		if($("#incrementalTypeId").is(':checked')){
			incrementalType="Y";
		}else{
			incrementalType="N";  // unchecked
		}
		
		var timeSeries="";
		if($("#none").is(':checked')){
			timeSeries="None,";
		}else{
			if($("#chkmonth").is(':checked')){
			timeSeries="month,";
			}
			if($("#chkDayOfWeek").is(':checked')){
			timeSeries=timeSeries+"dayOfWeek,";
			}
			if($("#chkHourOfDay").is(':checked')){
			timeSeries=timeSeries+"hourOfDay,";
			}
			if($("#chkDayOfMonth").is(':checked')){
			timeSeries=timeSeries+"dayOfMonth,";
			}
		}
		var abc=timeSeries.slice(0,-1);
		
		var startDate =  $("#startdateid").val();
		if(startDate==""){
			startDate="0000-00-00 00:00:00";
		}
		var endDate =  $("#enddateid").val();
		if(endDate==""){
			endDate="0000-00-00 00:00:00";
		} 
		var	frequencyDays = $("#EveryDay").val();
		if(frequencyDays==""){
			frequencyDays=0;
		}
		//alert(startDate);
		if (no_error) {

			var form_data = {
				idApp : $("#idApp").val(),
				modelGovernanceType : $("#apptypeid").val(),
				modelIdCol :  $("#modelIdCol").val(),
				decileCol :  $("#decileCol").val(),
				expectedPercentage : expectedPercentage,
				thresholdPercentage :  thresholdPercentage,
				buildHistoric : buildHistoric,
				incrementalType : incrementalType,
				startDate :  startDate,
				endDate :  endDate,
				dateFormat : $("#dateformatid").val(),
				timeSeries : abc,
				updateFrequency : $("#frequencyid").val(),
				frequencyDays : frequencyDays
				/* measurementMeanType :  $("#measurementMeanType").val(),
				measurementMeanThreshold :  $("#measurementMeanThreshold").val(),
				measurementStdDevType :  $("#measurementStdDevType").val(),
				measurementStdDevThreshold :  $("#measurementStdDevThreshold").val(),
				leftSourceId : $("#idData").val(),
				rightSourceId : $("#rightsourceid").val(),
				expression : $("#matchkeyfid").val() */
				
			};
			
			console.log(form_data); //return false;
			//alert(form_data);
			$.ajax({
				url : 'updateDataIntolistModelGovernance',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				datatype : 'json',
				data : form_data,
				success : function(message) {
					console.log(message);
					var j_obj = $.parseJSON(message);
					if (j_obj.hasOwnProperty('success')) {
						//alert(message);
						toastr.info("Validation Check Customized Successfully");
						setTimeout(function() {
							window.location.href = 'validationCheck_View';
							//window.location.reload();
						}, 1000);
					} else if (j_obj.hasOwnProperty('fail')) {
						toastr.info("There was a Problem,Please try again later");
						// console.log(j_obj.fail);
						//satoastr.info(j_obj.fail);
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					$('#initial').hide();
					$('#fail').show();
				}
			});
		}
		return false;
	});
	
	$("#scoreconsistencycreateid").click(function() {
		var no_error = 1;
		$('.input_error').remove();
	
		if($("#measurementexpression").val()==""){
			$('<span class="input_error" style="font-size:12px;color:red">Please enter Measurement Expression </span>').insertAfter($("#measurementexpression"));
		no_error=0;
		}
		
		if($("#matchingexpression").val()==""){
			$('<span class="input_error" style="font-size:12px;color:red"> Please enter Matching Expression </span>').insertAfter($("#matchingexpression"));
		no_error=0;
		}
		if($("#leftsourceslicestart").val()==""){
			$('<span class="input_error" style="font-size:12px;color:red"> Please Choose LeftSource Slice Start </span>').insertAfter($("#leftsourceslicestart"));
			no_error=0;
		}
		if($("#leftsourcesliceend").val()==""){
			$('<span class="input_error" style="font-size:12px;color:red"> Please Choose LeftSource Slice End </span>').insertAfter($("#leftsourcesliceend"));
			no_error=0;
		}
		if($("#rightsourceslicestart").val()==""){
			$('<span class="input_error" style="font-size:12px;color:red"> Please Choose RightSource Slice Start </span>').insertAfter($("#rightsourceslicestart"));
			no_error=0;
		}
		if($("#rightsourcesliceend").val()==""){
			$('<span class="input_error" style="font-size:12px;color:red"> Please Choose RightSource Slice End </span>').insertAfter($("#rightsourcesliceend"));
			no_error=0;
		}
		
		var leftSourceSliceStart = $("#leftsourceslicestart").val();
		var leftSourceSliceEnd = $("#leftsourcesliceend").val();
		
		var rightSourceSliceStart = $("#rightsourceslicestart").val();
		var rightSourceSliceEnd = $("#rightsourcesliceend").val();
		
		var matchingExpression = $("#matchingexpression").val();
		var measurementExpression = $("#measurementexpression").val();
		if (no_error) {

			var form_data = {
				idApp : $("#idApp").val(),
				modelGovernanceType : $("#apptypeid").val(),
				leftSourceSliceStart : leftSourceSliceStart,
				leftSourceSliceEnd : leftSourceSliceEnd,
				rightSourceSliceStart : rightSourceSliceStart,
				rightSourceSliceEnd : rightSourceSliceEnd,
				matchingExpression : matchingExpression,
				measurementExpression : measurementExpression,
				scThreshold : $("#scthresholdid").val(),
				sourceDateFormat : $("#sourcedateformatid").val(),
				modelIdCol : $("#modelIdCol").val(),
				decileCol : $("#decileCol").val()
			};

			console.log(form_data); //return false;
			//alert(form_data);
			$
					.ajax({
						url : 'updateDataIntolistModelGovernanceForScoreConsistency',
						type : 'POST',
						headers: { 'token':$("#token").val()},
						datatype : 'json',
						data : form_data,
						success : function(message) {
							console.log(message);
							var j_obj = $.parseJSON(message);
							if (j_obj.hasOwnProperty('success')) {
								//alert(message);
								toastr
										.info("Validation Check Customized Successfully");
								setTimeout(
										function() {
											window.location.href = 'validationCheck_View';
											//window.location.reload();
										}, 1000);
							} else if (j_obj
									.hasOwnProperty('fail')) {
								toastr
										.info("There was a Problem,Please try again later");
								// console.log(j_obj.fail);
								//satoastr.info(j_obj.fail);
							}
						},
						error : function(xhr, textStatus,
								errorThrown) {
							$('#initial').hide();
							$('#fail').show();
						}
					});
		}
		return false;
		
	});
	
</script>