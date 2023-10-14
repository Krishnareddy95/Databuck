
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
						<input type="hidden" id="apptype" name="apptype" value="${apptype}" /> 
							<input type="hidden" id="applicationName" value="${applicationName}" /> 
							<input	type="hidden" id="description" value="${description}" />
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
										<select class="form-control" id="decileequalityid" name="decileequalityid">
											<option value="-1">Select Application</option>
											<c:forEach var="decileEqualityAppType"
												items="${decileEqualityAppType}">
												<option value="${decileEqualityAppType.key}">${decileEqualityAppType.value}</option>
											</c:forEach>
										</select> 
										<label for="decileequalityid">Select Decile Equality Type of Application*</label>
										<!-- <span class="help-block">Short text about Data Set</span> -->
									</div>
								</div>
							</div>
							<br>
						</div>
						<div class="" id="decileEqualityConsistency">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="decileconsistencyid" name="decileconsistencyid">
											<option value="-1">Select Application</option>
											<c:forEach var="decileConsistencyAppType"
												items="${decileConsistencyAppType}">
												<option value="${decileConsistencyAppType.key}">${decileConsistencyAppType.value}</option>
											</c:forEach>
										</select> <label for="decileconsistencyid">Select Decile Consistency Type of Application*</label><br>
									</div>
								</div>
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="scoreconsistencyid" name="scoreconsistencyid">
											<option value="-1">Select Application</option>
											<c:forEach var="scoreConsistencyAppType"
												items="${scoreConsistencyAppType}">
												<option value="${scoreConsistencyAppType.key}">${scoreConsistencyAppType.value}</option>
											</c:forEach>
										</select> <label for="scoreconsistencyid">Select Score Consistency Type of Application*</label><br>
									</div>
								</div>

							</div>
										<div class="form-actions noborder align-center tab-one-save">
								<button type="submit" id="modelgovernancedashboard" class="btn blue">Save</button>
							</div>
						</div>
						
					</div>
				</div>
			</div>
		</div>
	</div>
	<input type="hidden" id="mgCustomizeView" value="${mgCustomizeView}">
	<input type="hidden" id="decileEquality" value="${decileEquality}">
	<input type="hidden" id="decileConsistency" value="${decileConsistency}">
	<input type="hidden" id="scoreConsistency" value="${scoreConsistency}">
	<!-- END EXAMPLE TABLE PORTLET-->
</div>
<jsp:include page="footer.jsp" />
<script>
$( document ).ready(function() {
    console.log( "ready!" );
    var mgCustomizeView=$('#mgCustomizeView').val();
    //alert(mgCustomizeView);
    if(mgCustomizeView.length>2){
    	 //alert(mgCustomizeView);
    	 var decileEquality=$('#decileEquality').val();
    	 var decileConsistency=$('#decileConsistency').val();
    	 var scoreConsistency=$('#scoreConsistency').val();
    	 $("#decileequalityid").val(decileEquality);
    	 $("#decileconsistencyid").val(decileConsistency);
    	 $("#scoreconsistencyid").val(scoreConsistency);
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
			$('#thresholdPercentage').val("");
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

	$("#modelgovernancedashboard").click(function() {
						var no_error = 1;
						$('.input_error').remove();
						
						if(!checkInputText()){ 
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}

						if ($("#decileequalityid").val() == "-1") {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Choose Decile Equality Type of Application</span>')
									.insertAfter($('#decileequalityid'));
							no_error = 0;
						}
						if ($("#decileconsistencyid").val() == "-1") {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Choose Decile Consistency Type of Application</span>')
									.insertAfter($('#decileconsistencyid'));
							no_error = 0;
						}
						if ($("#scoreconsistencyid").val() == "-1") {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Choose Score Consistency Type of Application</span>')
									.insertAfter($('#scoreconsistencyid'));
							no_error = 0;
						}
						var decileEquality = $("#decileequalityid").val();
						var decileConsistency = $("#decileconsistencyid").val();
						var scoreConsistency = $("#scoreconsistencyid").val();
						//alert(decileConsistency);
						
						var modelGovernanceDashboard=decileEquality+"-"+decileConsistency+"-"+scoreConsistency;
						//alert(modelGovernanceDashboard);
						if (no_error) {

							var form_data = {
								idApp : $("#idApp").val(),
								//modelGovernanceType : $("#apptypeid").val(),
								modelGovernanceDashboard : modelGovernanceDashboard
							};

							console.log(form_data); //return false;
							//alert(form_data);
							$
									.ajax({
										url : 'saveDataIntolistModelGovernanceForModelGovernanceDashboard',
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
														.info("Validation Check Created Successfully");
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
	/* 	if ($("#modelIdCol").val() == "-1") {
			$(
					'<span class="input_error" style="font-size:12px;color:red">Please Choose Model Id Col</span>')
					.insertAfter($('#modelIdCol'));
			no_error = 0;
		}
		if ($("#decileCol").val() == "-1") {
			$(
					'<span class="input_error" style="font-size:12px;color:red">Please Choose Decile Col</span>')
					.insertAfter($('#decileCol'));
			no_error = 0;
		} */
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
				measurementExpression : measurementExpression
			};

			console.log(form_data); //return false;
			//alert(form_data);
			$
					.ajax({
						url : 'saveDataIntolistModelGovernanceForScoreConsistency',
						type : 'POST',
						headers: { 'token':$("#token").val()},
						datatype : 'json',
						data : form_data,
						success : function(message) {
							console.log(message);
							var j_obj = $.parseJSON(message);
							if (j_obj.hasOwnProperty('success')) {
								//alert(message);
								toastr.info("Validation Check Created Successfully");
								setTimeout(
										function() {
											window.location.href = 'validationCheck_View';
											//window.location.reload();
										}, 1000);
							} else if (j_obj.hasOwnProperty('fail')) {
								toastr.info("There was a Problem,Please try again later");
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