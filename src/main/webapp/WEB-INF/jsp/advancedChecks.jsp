
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
				
					<div class="cotainer">
				
					<div class="tabbable tabbable-custom boxless tabbable-reversed">
						<ul class="nav nav-tabs" style="border-bottom: 1px solid #ddd;">
							<li style="width: 8.63%"	>
							<a onClick="validateHrefVulnerability(this)"  href="createValidationFoundationChecksTab?idApp=${idApp}&idData=${idData}" style="padding: 2px 2px;"><strong>Foundation checks</strong></a>
							</li>
						
							<li  style="width: 8%">
							<a onClick="validateHrefVulnerability(this)"  href="createValidationEssentialChecksTab?idApp=${idApp}&idData=${idData}" style="padding: 2px 2px;"><strong>Essential checks</strong></a>
							</li>
							
							<li class="active" style="width: 8%">							
							<a onClick="validateHrefVulnerability(this)"  href="createValidationAdvancedChecksTab?idApp=${idApp}&idData=${idData}" style="padding: 2px 2px;"><strong>Advanced checks</strong></a>
							</li>
						</ul>
					</div>
				</div>
					
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Data Quality
								Fingerprint</span>
						</div>
					</div>
					
					<div class="portlet-body form">
						<!-- <form action="createValidationCheckCustomize" method="POST"> -->
						<div class="form-body">
						<input type="hidden" id="idApp" name="idApp" value="${idApp}" />
							
							<input type="hidden" id="idData" name="idData" value="${idData}" />
						
						<div class="row">
								<div class="col-md-12">
									<div class="advancedChecks">
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="numericalStats"
														name="numericalStats" class="md-check" value="Y">
													<label for="numericalStats"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<!--  --><input type="text" class="form-control"
												id="numericalStatsText" name="numericalStatsText"> <label
												for="numericalStatsText">Numerical field stats and
												comparison</label>-->
												<label for="numericalStats">Distribution Check</label> 
										</div>
									</div>
									</div>
								</div>
							</div>
							<br />
							
							
							<div class="row">
								<div class="col-md-12">
								<div class="advancedChecks">
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="recordAnomalyid"
														name="recordAnomalyid" class="md-check" value="Y">
													<label for="recordAnomalyid"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
													id="recordAnomalyThresholdId"
													name="recordAnomalyThresholdId" value="2"> 
											<label for="recordAnomalyThresholdId">Record Anomaly</label>
										</div>
									</div>
									</div>
									
								</div>
							</div>

							<!-- <div class="row">
									<div class="col-md-12">
										<div class="col-md-1">
											<div class="form-group form-md-checkboxes">
												<div class="md-checkbox-list">
													<div class="md-checkbox">
														<input type="checkbox" id="dataDriftCheck"
															name="dataDriftCheck" class="md-check" value="Y">
														<label for="dataDriftCheck"> <span></span> <span
															class="check"></span> <span class="box"></span></label>
													</div>
												</div>
											</div>
										</div>
										<div class="col-md-5 col-capture">
											<div class="form-group form-md-line-input">
												<input type="text" class="form-control"
													id="dataDriftCheckText" name="dataDriftCheckText">
												<label for="dataDriftCheck">Data Drift Check</label>
											</div>
										</div>
									</div>
								</div> -->
							<br />
							
							<div class="row">
								<div class="col-md-12">
								<div class="advancedChecks">
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="timelinessKeyChk"
														name="timelinessKeyChk" class="md-check" value="Y">
													<label for="timelinessKeyChk"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
												id="timelinessKeyText" name="timelinessKeyText"> <label
												for="timelinessKeyText">Timeliness Check</label>
										</div>
									</div>
									</div>
								    	</div>
								</div>
							<br/>
							<div class="row">
								<div class="col-md-12">
								<div class="advancedChecks">
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="applyRules" name="applyRules"
														class="md-check" value="Y"> <label
														for="applyRules"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<!--  <input type="text" class="form-control"placeholder="Number of Rows" id="numberofRows" name="numberofRows"> -->
											<label for="applyRules">Apply Rules</label>
										</div>
									</div>
									<div class="col-md-1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="applyDerivedColumns"
														name="applyDerivedColumns" class="md-check" value="Y">
													<label for="applyDerivedColumns"> <span></span> <span
														class="check"></span> <span class="box"></span></label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-5 col-capture">
										<div class="form-group form-md-line-input">
											<!--  <input type="text" class="form-control"placeholder="Number of Rows" id="numberofRows" name="numberofRows"> -->
											<label for="applyDerivedColumns">Apply Derived
												Columns</label>
										</div>
									</div>
									</div>
									
								</div>
							</div>
							<br />
							
						
							
									
						
							
							
								<!-- ------------------ -->	

						</div>

						<div id="configProblem" class=" hidden">
						<div class="alert alert-warning">
						<h5 style="color:red" id="jsonmsg"></h5>
							<h5 style="color:red">
								&nbsp&nbsp&nbsp&nbsp Please make sure that relevant columns have
								been selected in data template for your validation check. <a onClick="validateHrefVulnerability(this)"
									href="dataSourceDisplayAllView?idData=${idData}"
									target="_blank">Click here</a> to make the changes.
							</h5>
						</div>
						</div>
						<div id="rulesProblem" class=" hidden">
						<div class="alert alert-warning">
						<h5 style="color:red" id="jsonmsgforrules"></h5>
							<h5 style="color:red">
								&nbsp&nbsp&nbsp&nbsp Please make sure that you have created rule
								for the selected data template. <a onClick="validateHrefVulnerability(this)"
									href="addNewRule"
									target="_blank">Click here</a> to add a new rule.
							</h5>
						</div>
						</div>
						<div id="derivedcolsProblem" class=" hidden">
						<div class="alert alert-warning">
						<h5 style="color:red" id="jsonmsgforderivedcols"></h5>
							<h5 style="color:red">
								&nbsp&nbsp&nbsp&nbsp Please make sure that you have created derived column
								for the selected data template. <a onClick="validateHrefVulnerability(this)"
									href="addNewExtendTemplate"
									target="_blank">Click here</a> to create derived column.
							</h5>
						</div>
						</div>
						<div id="incrementalProblem" class=" hidden">
						<div class="alert alert-warning">
						<h5 style="color:red" id="jsonmsgforincremental"></h5>
							<h5 style="color:red">
								&nbsp&nbsp&nbsp&nbsp Please change your Application type to Bulk Load.
							</h5>
						</div>
						</div>
						
							<div class="row">
									<div class="form-actions noborder">
										<button type="submit" id="createValidationCheck"
											class="btn blue btn-primary center-block">Submit</button>
									</div>
							</div>
						</div>
					</div>
				
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />
<script>
	$(document).ready(function() {
		var filesystem = $("#filesystem").val();
		console.log(filesystem);
		if (filesystem == "true") {
			$('#filesystemid').removeClass('hidden').show();
			$('#filesystemid1').removeClass('hidden').show();
		} else {
			$('#filesystemid').addClass("hidden");
			$('#filesystemid1').addClass("hidden");
		}
	});
	$("#ApplicationType").change(function() {
		var ApplicationType = $("#ApplicationType").val();
		//alert(ApplicationType);
		if (ApplicationType == "Historic") {
			$('#dateDivId').removeClass('hidden').show();
			$('#dataDriftDiv').removeClass('hidden').show();
		} else if (ApplicationType == "Ongoing"){
			$('#dataDriftDiv').removeClass('hidden').show();
			$('#dateDivId').addClass("hidden");
		}else {
			$('#dateDivId').addClass("hidden");
			$('#dataDriftDiv').removeClass('hidden').show();
		}
	});
	$("#createValidationCheck")
			.click(
					function() {
						
						if(!checkInputText()){ 
							alert('Vulnerability in submitted data, not allowed to submit!');
							return;
						}
						//now//alert( "Handler for .click() called." );
						var idApp = $('#idApp').val();
						var idData = $('#idData').val();

						var ApplicationType = $("#ApplicationType").val();
						var incrementalTypeId = "N";
						var buildHistoricId = "N";
						if (ApplicationType == "Historic") {
							incrementalTypeId = "Y";
							buildHistoricId = "Y";
						} else if (ApplicationType == "Ongoing") {
							incrementalTypeId = "Y";
						} else {

						}
						/* var buildHistoricId = "N";
						if ($('#buildHistoricId').is(":checked")) {
							buildHistoricId = "Y";
						}
						var incrementalTypeId = "N";
						if ($('#incrementalTypeId').is(":checked")) {
							incrementalTypeId = "Y";
						} */
						var startdateid = $('#startdateid').val();
						var enddateid = $('#enddateid').val();
						var dateformatid = $('#dateformatid').val();
						var recordCountAnomalyType = $(
								'#recordCountAnomalyType').val();
						var KeyBasedRecordCountAnomaly = "N";
						/* if (recordCountAnomalyType == "RecordCountAnomaly") {
							recordCountAnomalyType = "Y";
						} else {
							recordCountAnomalyType = "N";
							KeyBasedRecordCountAnomaly = "Y";
						} */

						var DFSetComparisonId = $('#DFSetComparisonId').val();
						var validityThresholdId = $('#validityThresholdId').val();
						var groupEquality = "N";
						if ($('#groupEquality').is(":checked")) {
							groupEquality = "Y";
						}
						var groupEqualityText = $('#groupEqualityText').val();
						var duplicateCount = "N";
						if ($('#duplicateCount').is(":checked")) {
							duplicateCount = "Y";
						}
						var duplicateCountText = $('#duplicateCountText').val();
						if(duplicateCountText==""){
							duplicateCountText=0;
						}
						var duplicateCountAll = "N";
						if ($('#duplicateCountAll').is(":checked")) {
							duplicateCountAll = "Y";
						}
						var duplicateCountAllText = $('#duplicateCountAllText')
								.val();
						if(duplicateCountAllText==""){
							duplicateCountAllText=0;
						}
						var dataDriftCheck = "N";
						if ($('#dataDriftCheck').is(":checked")) {
							dataDriftCheck = "Y";
						}
						var dataDriftCheckText = $('#dataDriftCheckText').val();
						var numericalStats = "N";
						if ($('#numericalStats').is(":checked")) {
							numericalStats = "Y";
						}
						var numericalStatsText = '0.0';
						var stringStat = "N";
						if ($('#stringStat').is(":checked")) {
							stringStat = "Y";
						}

						var stringStatText = $('#stringStatText').val();
						var columnOrderVal = "N";
						if ($('#columnOrderVal').is(":checked")) {
							columnOrderVal = "Y";
						}
						var fileNameVal = "N";
						if ($('#fileNameVal').is(":checked")) {
							fileNameVal = "Y";
						}
						var nameofEntityColumn = $('#nameofEntityColumn').val();
						
						if(nameofEntityColumn == ""){
							nameofEntityColumn = "NA";
						}
						
						var recordAnomalyThreshold = $('#recordAnomalyThresholdId').val();
						if(recordAnomalyThreshold == ""){
							recordAnomalyThreshold = 2.0;
						}
						var recordAnomalyid = "N";
						if ($('#recordAnomalyid').is(":checked")) {
							recordAnomalyid = "Y";
						}
						var nullCount = "N";
						if ($('#nullCount').is(":checked")) {
							nullCount = "Y";
						}
						var nullCountText = $('#nullCountText').val();
						var applyRules = "N";
						if ($('#applyRules').is(":checked")) {
							applyRules = "Y";
						}
						var applyDerivedColumns = "N";
						if ($('#applyDerivedColumns').is(":checked")) {
							applyDerivedColumns = "Y";
						}
						var csvDirectory = $('#csvDirectory').val();
						var frequencyid = $('#frequencyid').val();
						var EveryDay = $('#EveryDaytext').val();
						var dataCyclicality = "";
						if ($('#none').is(":checked")) {
							dataCyclicality = dataCyclicality + "None,";
						}
						if ($('#month').is(":checked")) {
							dataCyclicality = dataCyclicality + "month,";
						}
						if ($('#dow').is(":checked")) {
							dataCyclicality = dataCyclicality + "dayOfWeek,";
						}
						if ($('#hod').is(":checked")) {
							dataCyclicality = dataCyclicality + "hourOfDay,";
						}
						if ($('#dom').is(":checked")) {
							dataCyclicality = dataCyclicality + "dayOfMonth,";
						}
						dataCyclicality = dataCyclicality.substring(0,
								dataCyclicality.length - 1);
						if (dataCyclicality == "") {
							dataCyclicality = "None"
						}
						if (DFSetComparisonId == "") {
							DFSetComparisonId = 0.0;
						}
						if (validityThresholdId == ""){
							validityThresholdId = 1.0;
						}
						if (startdateid == "") {
							startdateid = null;
						}
						if (enddateid == "") {
							enddateid = null;
						}
						if (dataDriftCheckText == "") {
							dataDriftCheckText = 0.0;
						}

						var dupCheck = "N";
						if ($('#dupCheck').is(":checked")) {
							dupCheck = "Y";
						}
						
						var timelinessKeyChk = "N";
						if ($('#timelinessKeyChk').is(":checked")) {
							timelinessKeyChk = "Y";
						}
						
						var defaultCheck = "N";
						if ($('#defaultCheck').is(":checked")) {
							defaultCheck = "Y";
						}
						var defaultValues = $('#defaultValues').val();
						
						var patternCheck = "N";
						if ($('#patternCheck').is(":checked")) {
							patternCheck = "Y";
						}
						var dateRuleChk = "N";
						if ($('#dateRuleChk').is(":checked")) {
							dateRuleChk = "Y";
						}
						var badData = "N";
						if($('#badData').is(":checked")){
							badData = "Y";
						}
						
						//priyanka 25-12-2018 
						
						var lengthCheck = "N";
						if($('#lengthCheck').is(":checked")){
							lengthCheck = "Y";
						}
						
						var maxLengthCheck = "N";
						if($('#maxLengthCheck').is(":checked")){
							maxLengthCheck = "Y";
						}
						
						var dGroupNullCheck = "N";
						console.log("dGroupNullcheck :"+($('#dGroupNullCheck').is(":checked")))
						if($('#dGroupNullCheck').is(":checked")){
							dGroupNullCheck = "Y";
						}
						
						var dGroupDateRuleCheck = "N";
						console.log("dGroupDateRuleCheck :"+($('#dGroupDateRuleCheck').is(":checked")))
						if($('#dGroupDateRuleCheck').is(":checked")){
							dGroupDateRuleCheck = "Y";
						}
						
						//alert("dataCyclicality="+dataCyclicality);
						//alert(KeyBasedRecordCountAnomaly);
						var listApplications = {

							idApp : idApp,
							idData : idData,
							numericalStatCheck : numericalStats,
							numericalStatThreshold : numericalStatsText,
							recordAnomalyCheck : recordAnomalyid,
							recordAnomalyThreshold : recordAnomalyThreshold,
							applyRules : applyRules,
							applyDerivedColumns : applyDerivedColumns,
							timelinessKeyChk : timelinessKeyChk
						};
						$('#jsonmsg').html('');
				$('#configProblem').addClass('hidden');
				$('#rulesProblem').addClass('hidden');
				$('#derivedcolsProblem').addClass('hidden');
				$('#incrementalProblem').addClass('hidden');
						console.log(listApplications);
						$('button').prop('disabled', true);
						$.ajax({
									url : './saveValidationAdvancedChecksAjax',
									type : 'POST',
									headers: { 'token':$("#token").val()},
									datatype : 'json',
									data : JSON.stringify(listApplications),
									contentType : "application/json",
									
									success : function(message) {
										var j_obj = $.parseJSON(message);
										if (j_obj.hasOwnProperty('success')) {

											$('#configProblem').addClass(
													'hidden');
											$('#rulesProblem').addClass(
											'hidden');
											$('#derivedcolsProblem').addClass(
											'hidden');
											toastr
													.info('Validation Check Created Successfully');
											setTimeout(function() {
												window.location.href = "validationCheck_View";

											}, 1000);
										} else if (j_obj.hasOwnProperty('fail')) {
											$('#configProblem').removeClass(
													'hidden').show();
											var msg = j_obj['fail']
											console.log(msg);
											$('#jsonmsg').html(
													"<i class='fa fa-warning'></i> "
															+ msg);
											console.log(j_obj.fail);
											toastr.info(j_obj.fail);
									$('#rulesProblem').addClass(
									'hidden');
									$('#derivedcolsProblem').addClass(
									'hidden');
									$('#incrementalProblem').addClass('hidden');
									$('button').prop('disabled', false);
										}
										 else if (j_obj.hasOwnProperty('rules')) {
												$('#rulesProblem').removeClass(
														'hidden').show();
												var msg = j_obj['rules']
												console.log(msg);
												$('#jsonmsgforrules').html(
														"<i class='fa fa-warning'></i> "
																+ msg);
												console.log(j_obj.rules);
												toastr.info(j_obj.rules);
												$('#configProblem').addClass(
												'hidden');
										$('#derivedcolsProblem').addClass(
										'hidden');
										$('#incrementalProblem').addClass('hidden');
										$('button').prop('disabled', false);
											}
										 else if (j_obj.hasOwnProperty('derivedcols')) {
												$('#derivedcolsProblem').removeClass(
														'hidden').show();
												var msg = j_obj['derivedcols']
												console.log(msg);
												$('#jsonmsgforderivedcols').html(
														"<i class='fa fa-warning'></i> "
																+ msg);
												console.log(j_obj.derivedcols);
												toastr.info(j_obj.derivedcols);
										$('#configProblem').addClass('hidden');
										$('#rulesProblem').addClass('hidden');
										$('#incrementalProblem').addClass('hidden');
										$('button').prop('disabled', false);
											}
										 else if (j_obj.hasOwnProperty('incremental')) {
												$('#incrementalProblem').removeClass(
														'hidden').show();
												var msg = j_obj['incremental']
												console.log(msg);
												$('#jsonmsgforincremental').html(
														"<i class='fa fa-warning'></i> "
																+ msg);
												console.log(j_obj.incremental);
												toastr.info(j_obj.incremental);
										$('#configProblem').addClass('hidden');
										$('#rulesProblem').addClass('hidden');
										$('#derivedcolsProblem').addClass('hidden');
										$('button').prop('disabled', false);
											}
									},
									error : function(xhr, textStatus,
											errorThrown) {
										$('#initial').hide();
										$('#fail').show();
									}
								});
					});

	$('#frequencyid').change(function() {
		$this = $(this);
		var selectedVal = $this.val();
		if (selectedVal == 'everyXdays') {
			$('#EveryDay').removeClass('hidden').show();
		} else {
			$('#EveryDay').addClass('hidden');
		}
	});
	/* $("#buildHistoricId").click(function() {
		if ($('#buildHistoricId').is(':checked')) {
			$('#dateDivId').removeClass('hidden').show();
		} else {
			$('#dateDivId').addClass("hidden");
		}
	}); */
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
	$('#recordCountAnomalyType').change(function() {
		$this = $(this);
		var selectedVal = $this.val();

		if (selectedVal == 'KeyBasedRecordCountAnomaly') {
			$('#groupEqualitydiv').removeClass('hidden').show();
		} else {
			$('#groupEqualitydiv').addClass('hidden');
		}
	});
	/* $("#month").click(function() { 
		if ($('#month').is(':checked'))	{
		$('#monthchecked').removeClass('hidden').show();
	} else {
		$('#monthchecked').addClass('hidden');
	}
	});
	$("#dow").click(function() { 
		if ($('#dow').is(':checked'))	{
		$('#dowchecked').removeClass('hidden').show();
	} else {
		$('#dowchecked').addClass('hidden');
	}
	});
	
	$("#hod").click(function() { 
		if ($('#hod').is(':checked'))	{
		$('#hodchecked').removeClass('hidden').show();
	} else {
		$('#hodchecked').addClass('hidden');
	}
	});
	
	$("#dom").click(function() { 
		if ($('#dom').is(':checked'))	{
		$('#domchecked').removeClass('hidden').show();
	} else {
		$('#domchecked').addClass('hidden');
	}
	}); */
</script>