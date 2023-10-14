
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	select ~ div > ul {
  		width: 450px;
	}
	select ~ div > button {
  		width: 450px !important;
	}
</style>
<head>

</head>
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
							value="${description}" /> <input type="hidden" id="idDM"
							name="idDM" value="${idDM}" /> <input type="hidden"
							id="incrementalMatching" name="incrementalMatching"
							value="${incrementalMatching}" />
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
									<select class="form-control text-left" style="text-align: left;" id="targetSchemaList" >
									</select> 
									<input type="hidden" class="form-control" id="targetSchemaId"
										name="targetSchema">
									<label for="form_control">Target Connection *</label>
								</div>
							</div>
						</div>
						<br> <br>
						<div class="row">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<select class="form-control" id="rollTypeId" name="rollType"
												placeholder="Choose Type">
												<option value="current_previous">CurrentVsPrevious</option>
												<option value="adhoc_match">Adhoc Match</option>
									</select> <label for="form_control_1">Roll Type *</label>
								</div>
							</div>
						</div>
						<br> <br>
						<div class="row">
							<div class="col-md-12">
								<div class="tabbable tabbable-custom boxless tabbable-reversed">
									<ul class="nav nav-tabs" style="border-bottom: 1px solid #ddd;">
										<li class="active" style="width: 100%"><a onClick="validateHrefVulnerability(this)"  href="#"
											style="padding: 10px 2px; text-align: center;"><strong>
													Matching Type & Matching Keys</strong> </a></li>
									</ul>
								</div>
							</div>
						</div>
						<div class="row hidden">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<select class="form-control" id="mcardinalityid"
										name="mcardinality">
										<!-- <option value="-1">Choose Match Category</option> -->
										<option value="One to One">One to One</option>
										<!-- <option value="One to Many">One to Many</option> -->
										<!-- <option value="Many to Many">Many to Many</option> -->
									</select> <label for="form_control_1">Match Category *</label>
								</div>
								<br /> <span class="required"></span>
							</div>
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<select class="form-control" id="matchtypeid" name="matchtype">
										<!-- <option value="-1">Choose Match Type</option> -->
										<option value="Key Fields Match">Key Fields Match</option>
										<option value="Measurements Match">Measurements Match</option>
									</select> <label for="form_control_1">Match Type *</label>
								</div>
								<br /> <span class="required"></span>
							</div>
						</div>

						<br />
						<div class="row">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control" id="descriptionid"
										name="description" value="${applicationName}" readonly /> <label
										for="form_control">First Source Template </label>
								</div>
							</div>

							<div class="row">
								<div class="col-md-6 col-capture non-schema-matching">
									<div class="form-group form-md-line-input">
										<select class="form-control text-left"
											style="text-align: left;" id="rightsourceid"
											name="rightsource">

										</select> <label for="form_control_1">Second Source Template *</label><input
											type="hidden" id="selectedSecondSrcTemplate"
											name="selectedSecondSrcTemplate" value="" />

									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<div class="col-wrapper">
								<span style="display: none" class="formid">leftsiderule</span>
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control functionslist"
												name="leftfunction">
												<option value="-1">Select Function</option>
												<c:forEach var="listRefFunctionsname"
													items="${listRefFunctionsname}">
													<option value="${listRefFunctionsname.name}">${listRefFunctionsname.name}</option>
												</c:forEach>
											</select> <label for="leftfunid">Select Function</label>
										</div>
										<br /> <span class="required"></span>
									</div>

									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control functionslist"
												name="rightfunction" placeholder="">
												<option value="-1">Select Function</option>
												<c:forEach var="listRefFunctionsname"
													items="${listRefFunctionsname}">
													<option value="${listRefFunctionsname.name}">${listRefFunctionsname.name}</option>
												</c:forEach>
											</select> <label for="form_control_1">Select Function</label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
							</div>
							<!-- col-wrapper -->

							<div class="row">

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="leftitemid" name="column">
											<option value="-1">Select Column</option>
											<c:forEach var="listDataDefinitionColumnNames"
												items="${listDataDefinitionColumnNames}">
												<option value="${listDataDefinitionColumnNames}">${listDataDefinitionColumnNames}</option>
											</c:forEach>
										</select> <label for="columnid">Select Data Column *</label><br>
									</div>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="rightitemid" name="rightitem">
										</select> <label for="form_control_1">Select Data Column *</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>
							<div class="row">
								<div class="col-md-6 col-capture" id="queryid1">
									<div class="form-group form-md-checkboxes">
										<div class="md-checkbox-list">
											<div class="md-checkbox">
												<input type="checkbox" class="md-check" id="recordCountId"
													name="recordCountId" value="Y" disabled="disabled" checked />
												<label for="recordCountId"><span></span> <span
													class="check"></span> <span class="box"></span>Record Count
													Matching </label>
											</div>
										</div>
									</div>
								</div>
								<div class="col-md-6 col-capture" id="queryid1">
									<div class="form-group form-md-checkboxes">
										<div class="md-checkbox-list">
											<div class="md-checkbox">
												<input type="checkbox" class="md-check" id="primaryKeyId"
													name="primaryKeyId" value="Y" /> <label for="primaryKeyId"><span></span>
													<span class="check"></span> <span class="box"></span>Primary
													Key Matching </label>
											</div>
										</div>
									</div>
								</div>
							</div>

							<div class="form-body" id="s4_form_id">
								<div class="row">
									<div class="col-md-12 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="matchkeyfid" name="matchkeyformula"
												placeholder="Ex: leftId=rightId&&leftAccount=rightAccount" />
											<label for="form_control_1">Key Matching Rule</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
							</div>
							<div id="leftsource" class=" hidden">
								<div class="alert alert-warning">
									<h5 style="color: red" id="jsonmsgforleftsource"></h5>
									<h5 style="color: red"></h5>
								</div>
							</div>
							<div class="form-actions noborder align-center tab-one-save">
								<button type="submit" id="matchkeycreateid" class="btn blue">Submit</button>
							</div>
							<c:if test="${matchingRulesTrue eq 'true'}">
								<div class="portlet-title">
									<div class="caption font-red-sunglo">
										<span class="caption-subject bold ">Matching Rules </span>
									</div>
								</div>
								<hr />
								<div class="portlet-body">
									<table
										class="table table-striped table-bordered  table-hover datatable-master"
										style="width: 100%;">
										<thead>
											<tr>
												<th>Match Type</th>
												<th>Left Side Expression</th>
												<th>Right Side Expression</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="matchingRulesObj" items="${matchingRules}">
												<tr>
													<td>${matchingRulesObj.matchType}</td>
													<td>${matchingRulesObj.leftSideExp}</td>
													<td>${matchingRulesObj.rightSideExp}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</c:if>

						</div>
					</div>
				</div>
			</div>
		</div>


		<!-- END EXAMPLE TABLE PORTLET-->
	</div>
	<jsp:include page="footer.jsp" />
	<script src="./assets/global/plugins/jquery-ui.min.js"></script>
	<script
		src="./assets/global/plugins/bootstrap-multiselect.js"></script>
	<link rel="stylesheet"
		href="./assets/global/plugins/bootstrap-multiselect.css">
	<script>
 
	$(document).ready(function() {
		//$('#lstTable').multiselect();
		$('#rightsourceid').multiselect({
			maxHeight : 200,
			buttonWidth : '500px',
			includeSelectAllOption : true,
			enableFiltering : true,
			
			nonSelectedText : 'Select Template'
			 
		});
		
		$('#targetSchemaList').multiselect({
			maxHeight : 200,
			buttonWidth : '500px',
			includeSelectAllOption : true,
			enableFiltering : true,
			nonSelectedText: 'Select Schema'
		});
		
		$('#primaryKeyId').change(
			function() {
				if($('#primaryKeyId').is(':checked')){
					$('#s4_form_id').addClass('hidden');
					$('#matchkeyfid').val('');
				} else {
					$('#s4_form_id').removeClass('hidden');
				}
		});
		
	});
</script>
	<script>
		$("#matchkeycreateid").click(function() {
							var no_error = 1;
							$('.input_error').remove();
							
							if(!checkInputText()){ 
								no_error = 0;
								alert('Vulnerability in submitted data, not allowed to submit!');
							}
							
							if ($("#rightsourceid").val() == -1) {
								$('<span class="input_error" style="font-size:12px;color:red">Please Choose Second Source Template</span>')
										.insertAfter($('#rightsourceid'));
								no_error = 0;
							}
							
							if (no_error) {

								var recordCount = "";
								if ($('#recordCountId')
										.is(":checked")) {
									recordCount = "Y";
								} else {
									recordCount = "N";
								}
								var primaryKey = "";
								if ($('#primaryKeyId')
										.is(":checked")) {
									primaryKey = "Y";
								} else {
									primaryKey = "N";
								}
								var form_data = {
  									idApp : $("#idApp").val(),
									idData : $("#idData").val(),
									leftSourceId : $("#idData").val(),
									rightSourceId : $("#rightsourceid").val(),
									expression : $("#matchkeyfid").val(),
									matchType : $("#matchtypeid").val(),
									recordCount : recordCount,
									primaryKey : primaryKey,
									targetSchemaId: $("#targetSchemaId").val(),
									rollType :  $("#rollTypeId").val()
								// times: new Date().getTime()
								};
								$('#leftsource').addClass('hidden');
								$('button').prop('disabled', true);
								console.log(form_data); //return false;
								$
										.ajax({
											url : './saveRollDataIntoListDMCriteria',
											type : 'POST',
											headers: { 'token':$("#token").val()},
											datatype : 'json',
											data : form_data,
											success : function(message) {
												console.log(message);
												var j_obj = $
														.parseJSON(message);
												if (j_obj.hasOwnProperty('success')) {
													toastr.info("Match Key created successfully");
													setTimeout(function() {
																window.location.href = 'validationCheck_View';
															}, 1000);
												} else if (j_obj.hasOwnProperty('fail')) {
													$('button').prop('disabled', false);
													toastr.info("No Matching key found");
												} else if (j_obj
														.hasOwnProperty('leftsource')) {
													$('button').prop('disabled', false);
													$('#leftsource').removeClass('hidden').show();
													var msg = j_obj['leftsource']
													console.log(msg);
													$('#jsonmsgforleftsource')
															.html("<i class='fa fa-warning'></i> "+ msg
																			+ "<a onClick='validateHrefVulnerability(this)'  href='dataSourceDisplayAllView?idData="
																			+ $("#idData").val()
																			+ "'	target='_blank'>Click here</a> to make the changes.");
												} else if (j_obj.hasOwnProperty('rightsource')) {
													$('button').prop('disabled', false);
													$('#leftsource').removeClass('hidden').show();
													var msg = j_obj['rightsource']
													console.log(msg);
													$('#jsonmsgforleftsource')
															.html("<i class='fa fa-warning'></i> "+ msg
																			+ "<a onClick='validateHrefVulnerability(this)'  href='dataSourceDisplayAllView?idData="
																			+ $("#rightsourceid").val()
																			+ "'	target='_blank'>Click here</a> to make the changes.");
													console.log(j_obj.rightsource);
													toastr.info(j_obj.rightsource);
												}
											},
											error : function(xhr, textStatus,errorThrown) {
												$('#initial').hide();
												$('#fail').show();
											}
										});
							}
							return false;
						});
	//call for get Second Source Template name
	$(document).ready(function() {

		$.ajax({
			url : './secondSourceTemplateName',
			type : 'GET',
			datatype : 'json',
			success : function(obj) {
				$(obj).each(function(i, item) {
					var idAppVal = item.substring(0 , item.indexOf("-"));
					var TemplateNameVal=item.substring((item.indexOf("-"))+1,item.length);
			
				$('#rightsourceid').append($('<option>', {
					value : idAppVal,
					text : TemplateNameVal
				}));
			 });

				$('#rightsourceid').multiselect('rebuild');
				$('#rightsourceid').multiselect({
					includeSelectAllOption : true
				});
			},
			error : function() {
			},
		});

	});


	$('#rightsourceid').on('change', function() {

		var currentSelection;

		var currentValues = $(this).val();
		if (currentValues.length == 1) {
			currentSelection = currentValues;
		}
		selectedOption = $(this).val();

		$("#selectedSecondSrcTemplate").val(JSON.stringify(selectedOption));

	});
	
	// Get the schema list
$(document).ready(
		function() {
			$.ajax({
				url : './getRollDataTargetSchemaList',
				type : 'GET',
				datatype : 'json',
				success : function(message) {
							var j_obj = $.parseJSON(message);
							
							if (j_obj.hasOwnProperty('success')) {
								var temp = {};
								var count = 0;
								column_array = JSON.parse(j_obj.success);
								
								// empty the list from previous selection
								$("#targetSchemaList").empty();
								
								$.each(column_array, function(k1, v1) {
									var column_details = column_array[k1];
									var sourcename;
									if(count == 0){
										$("#targetSchemaId").val(k1);
									}
									temp_name = column_details;
									sourcename = temp_name;
									$("<option />").attr("value", k1).html(
											temp_name).appendTo("#targetSchemaList");
									temp[this.value] = this.subitems;
									count = count + 1;
								});
								
								$('#targetSchemaList').multiselect('rebuild');
								$('#targetSchemaList').multiselect({
									includeSelectAllOption : true
								});
							} else if (j_obj.hasOwnProperty('fail')) {
								console.log(j_obj.fail);
								toastr.info(j_obj.fail);
							}
						},
						error : function() {

						},

					});

				});

		var selectedOption;
		$('#targetSchemaList').on('change', function() {
			var currentSelection;
			var currentValues = $(this).val();

			if (currentValues.length == 1) {
				currentSelection = currentValues;
			}
			selectedOption = $(this).val();

			$("#targetSchemaId").val(selectedOption);
		});
	</script>