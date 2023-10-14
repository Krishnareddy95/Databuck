
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
							value="${description}" />
							 <input type="hidden" id="idDM"	name="idDM" value="${idDM}" />
							  <input type="hidden" id="incrementalMatching" name="incrementalMatching" value="${incrementalMatching}" />
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
										<input type="text" class="form-control" id="descriptionid"
											name="description" value="${applicationName}" readonly>
										<label for="form_control">Data Template</label>
										<!-- <span class="help-block">Short text about Data Set</span> -->
									</div>
								</div>
							</div>

							<!--  <div class="form-actions noborder align-center">
                                        <button type="submit" id="appcreateid" class="btn blue">Submit</button>
                                    </div> -->
							<br> <br>
							<div class="row">
								<div class="col-md-12">
									<div class="tabbable tabbable-custom boxless tabbable-reversed">
										<ul class="nav nav-tabs"
											style="border-bottom: 1px solid #ddd;">
											<!-- <li style="width: 48%" disabled="disabled"><a onClick="validateHrefVulnerability(this)"  href="#"
												style="padding: 10px 2px;">
													<button type="button"
														class="btn btn-top-menu btn-circle red">1</button> <strong>
														Matching Type </strong>
											</a></li> -->
											<li class="active" style="width: 100%"><a onClick="validateHrefVulnerability(this)"  href="#"
												style="padding: 10px 2px; text-align: center;"> <!-- <button type="button"
														class="btn btn-top-menu btn-circle red"></button> --> <strong>
														Matching Type & Matching Keys</strong>
											</a></li>
											<!-- <li style="width:25%">
                                                                    <a onClick="validateHrefVulnerability(this)"  href="#" style="padding:10px 2px;" disabled="disabled">
                                                                    <strong>Matching Results </strong></a>
                                                                </li> -->
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
											<option value="Measurements Match">Measurements
												Match</option>
										</select> <label for="form_control_1">Match Type *</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>
							<div class="row">
								<div class="col-md-6 col-capture" id="queryid1">
									<div class="form-group form-md-checkboxes">
										<div class="md-checkbox-list">
											<div class="md-checkbox">
												<input type="checkbox" class="md-check" id="measurementid"
													name="measurementid" value="Y"> <label
													for="measurementid"><span></span> <span
													class="check"></span> <span class="box"></span>Enable
													Match by Value</label>
											</div>
										</div>
									</div>
								</div>
								<div class="col-md-6 col-capture" id="queryid1">
									<div class="form-group form-md-checkboxes">
										<div class="md-checkbox-list">
											<div class="md-checkbox">
												<input type="checkbox" class="md-check" id="groupbyid"
													name="groupbyid" value="Y"> <label for="groupbyid"><span></span>
													<span class="check"></span> <span class="box"></span>Enable
													Match by Microsegment</label>
											</div>
										</div>
									</div>
								</div>
								<div class="col-md-6 col-capture hidden" id="groupbythresholdid">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
												id="absoluteThresholdId" name="absoluteThresholdId"
												placeholder="Absolute Threshold"> <label
												for="absoluteThresholdId"></label>
										</div>
									</div>
							</div>
							<br />
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="descriptionid"
											name="description" value="${idData}_${applicationName}" readonly /> <label
											for="form_control">First Source Template </label>
										<!-- <span class="help-block">Short text about Data Set</span> -->
									</div>
								</div>

							<%--	<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="rightsourceid"
											name="rightsource">
											<option value="-1">Select Second Source Template</option>
											<c:forEach var="getlistdatasourcesnameobj"
												items="${getlistdatasourcesname}">
												<option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.name}</option>
											</c:forEach>
										</select> <label for="form_control_1">Second Source Template *</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>--%>
	<!--  id="lstSecondSrcTemplate" -->
						 	<div class="row">
										<div
										class="col-md-6 col-capture non-schema-matching">
											<div class="form-group form-md-line-input">
											<select class="form-control text-left"
													style="text-align: left;"  id="rightsourceid"
											name="rightsource" >
											<option value='-1'> Select Second Source Template </option>
											</select> <label for="form_control_1">Second Source Template *</label><input type="hidden" id="selectedSecondSrcTemplate" name="selectedSecondSrcTemplate" value=""/>
									
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
													<input type="checkbox" class="md-check"
														id="recordCountId"
														name="recordCountId" value="Y" disabled="disabled" checked/> <label
														for="recordCountId"><span></span> <span
														class="check"></span> <span class="box"></span>Record Count Matching
														</label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-6 col-capture" id="queryid1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" class="md-check"
														id="primaryKeyId"
														name="primaryKeyId" value="Y" /> <label
														for="primaryKeyId"><span></span> <span
														class="check"></span> <span class="box"></span>Primary Key Matching
														</label>
												</div>
											</div>
										</div>
									</div>
								</div>
							<div class="row">
									<div class="col-md-6 col-capture" id="queryid1">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" class="md-check"
														id="matchingRuleAutomaticid"
														name="matchingRuleAutomaticidname" value="Y" /> <label
														for="matchingRuleAutomaticid"><span></span> <span
														class="check"></span> <span class="box"></span>Set
														Matching Rules Automatically</label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-6 col-capture" >
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
												id="UnMatchedAnomalyThresholdId" name="UnMatchedAnomalyThresholdId"
												placeholder="UnMatched Anomaly Threshold"> <label
												for="UnMatchedAnomalyThresholdId"></label>
										</div>
									</div>
								</div>
								<c:if test="${Source2DateFormat eq true }">
								
								<div class="row hidden">
								<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="dateformatid"
												name="dateformatid1" placeholder="Enter Date Format" value="" />
											<label for="form_control">Date Format for Second Source</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								<div class="col-md-6 col-capture">
											<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="rightsliceend"
												name="rightsliceend" placeholder="Please Enter Second Source Slice End" value="-1">
											<label for="form_control">Second Source Slice End</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
										</div>
								</div>
								</c:if>
							<div class="form-body" id="s4_form_id">
								<div class="row">
									<div class="col-md-12 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="matchkeyfid" name="matchkeyformula" placeholder="Ex: leftId=rightId&&leftAccount=rightAccount" /> <label
												for="form_control_1">Key Matching Rule</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
							</div>
							<div id="leftsource" class=" hidden">
								<div class="alert alert-warning">
									<h5 style="color: red" id="jsonmsgforleftsource"></h5>
									<h5 style="color: red">
										<%-- &nbsp&nbsp&nbsp&nbsp Please make sure that relevant columns have
								been selected in data template for your validation check. <a onClick="validateHrefVulnerability(this)"
									href="dataSourceDisplayAllView?idData=${idData}"
									target="_blank">Click here</a> to make the changes. --%>
									</h5>
								</div>
							</div>
							<div class="form-actions noborder align-center tab-one-save">
								<button type="submit" id="matchkeycreateid" class="btn blue">Submit</button>
							</div>
							<c:if test="${matchingRulesTrue eq 'true'}">
								<div class="portlet-title">
									<div class="caption font-red-sunglo">
										<span class="caption-subject bold ">Matching Rules<!-- <a onClick="validateHrefVulnerability(this)"  href="javascript:;">(Download CSV)</a> -->
										</span>
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
<link rel="stylesheet" href="./assets/global/plugins/bootstrap-multiselect.css">
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
	});
</script>
<script>
		$('#matchingRuleAutomaticid').click(function() {
			var check = "";
			if ($(this).is(':checked')) {
				// alert("Are you sure?");
				check = "Y";
				$('#s4_form_id').addClass('hidden');
			} else {
				check = "N";
				$('#s4_form_id').removeClass('hidden').show();
			}

		});
		$("#measurementid").change(function() {
		    if(this.checked) {
		    	$('#groupbythresholdid').removeClass('hidden');
		    }else{
		    	$('#groupbythresholdid').addClass('hidden');
		    }
		});
		//console.log(check);
		$("#matchkeycreateid").click(function() {
							var no_error = 1;
							$('.input_error').remove();
							
							if(!checkInputText()){ 
								no_error = 0;
								alert('Vulnerability in submitted data, not allowed to submit!');
							}

							/*if($("#matchkeyfid").val().length == 0){
							    $('<span class="input_error" style="font-size:12px;color:red">Please enter Matching key Expression to Continue</span>').insertAfter($('#matchkeyfid'));
							    no_error = 0;
							}*/
							if ($("#rightsourceid").val() == -1) {
								$('<span class="input_error" style="font-size:12px;color:red">Please Choose Second Source Template</span>')
										.insertAfter($('#rightsourceid'));
								no_error = 0;
							}
							
							//alert($("#matchingRuleAutomaticid").not(":checked"));
							 /*  if ((!$("#matchingRuleAutomaticid").is(":checked")) || ($("#matchkeyfid").val() == "")) { 
								 alert("key");
								 $('<span class="input_error" style="font-size:12px;color:red">Please Enter Key Matching Rule</span>')
									.insertAfter($('#matchkeyfid'));
								 no_error = 0;
							 }  */
							  
							/* if ($("#matchtypeid").val() == -1) {
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please Choose a Match Type</span>')
										.insertAfter($('#matchtypeid'));
								no_error = 0;
							} */
							if (no_error) {

								var check = "";
								if ($('#matchingRuleAutomaticid')
										.is(":checked")) {
									check = "Y";
								} else {
									check = "N";
								}
								var measurementid = "";
								if ($('#measurementid').is(":checked")) {
									measurementid = "Y";
								} else {
									measurementid = "N";
								}
								var groupbyid = "";
								if ($('#groupbyid').is(":checked")) {
									groupbyid = "Y";
								} else {
									groupbyid = "N";
								}
								var absoluteThresholdId = $("#absoluteThresholdId").val();
								if (absoluteThresholdId == "") {
									absoluteThresholdId = 0.0
								}
								var UnMatchedAnomalyThresholdId = $("#UnMatchedAnomalyThresholdId").val();
								if (UnMatchedAnomalyThresholdId == "") {
									UnMatchedAnomalyThresholdId = 0.0
								}
								var dateFormat = $("#dateformatid").val();
								if(dateFormat==undefined){
									dateFormat="";
								}
								var rightsliceend = $("#rightsliceend").val();
								if(rightsliceend==undefined){
									rightsliceend="";
								}
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
									matchingRuleAutomatic : check,
									idApp : $("#idApp").val(),
									idData : $("#idData").val(),
									//idDM  : $("#idDM").val(),
									//$( "#myselect" ).val(),
									leftSourceId : $("#idData").val(),
									rightSourceId : $("#rightsourceid").val(),
									expression : $("#matchkeyfid").val(),
									matchType : $("#matchtypeid").val(),
									absoluteThresholdId : absoluteThresholdId,
									unMatchedAnomalyThreshold : UnMatchedAnomalyThresholdId,
									groupbyid : groupbyid,
									measurementid : measurementid,
									dateFormat : dateFormat,
									incrementalMatching: $("#incrementalMatching").val(),
									rightSliceEnd: rightsliceend,
									recordCount : recordCount,
									primaryKey : primaryKey

								// times: new Date().getTime()
								};
								$('#leftsource').addClass('hidden');
								$('button').prop('disabled', true);
								console.log(form_data); //return false;
								$
										.ajax({
											url : './saveDataIntoListDMCriteria',
											type : 'POST',
											headers: { 'token':$("#token").val()},
											datatype : 'json',
											data : form_data,
											success : function(message) {
												console.log(message);
												var j_obj = $
														.parseJSON(message);
												if (j_obj
														.hasOwnProperty('success')) {
													//alert(message);
													toastr
															.info("Match Key created successfully");
													setTimeout(
															function() {
																window.location.href = 'validationCheck_View';
																//window.location.reload();
															}, 1000);
												} else if (j_obj
														.hasOwnProperty('fail')) {
													$('button').prop('disabled', false);
													toastr
															.info("No Matching key found");
													// console.log(j_obj.fail);
													//satoastr.info(j_obj.fail);
												} else if (j_obj
														.hasOwnProperty('leftsource')) {
													$('button').prop('disabled', false);
													$('#leftsource')
															.removeClass(
																	'hidden')
															.show();
													var msg = j_obj['leftsource']
													console.log(msg);
													if (j_obj
															.hasOwnProperty('match')){
														$('#jsonmsgforleftsource')
														.html(
																"<i class='fa fa-warning'></i> "
																		+ msg);
														
													}else{
													   $('#jsonmsgforleftsource')
															.html(
																	"<i class='fa fa-warning'></i> "
																			+ msg
																			+ "<a onClick='validateHrefVulnerability(this)'  href='dataSourceDisplayAllView?idData="
																			+ $(
																					"#idData")
																					.val()
																			+ "'	target='_blank'>Click here</a> to make the changes.");
													}
													console
															.log(j_obj.leftsource);
													toastr
															.info(j_obj.leftsource);
												} else if (j_obj
														.hasOwnProperty('rightsource')) {
													$('button').prop('disabled', false);
													$('#leftsource')
															.removeClass(
																	'hidden')
															.show();
													var msg = j_obj['rightsource']
													console.log(msg);
													if (j_obj
															.hasOwnProperty('match')){
														$('#jsonmsgforleftsource')
														.html(
																"<i class='fa fa-warning'></i> "
																		+ msg);
														
													}else{
													   $('#jsonmsgforleftsource')
															.html(
																	"<i class='fa fa-warning'></i> "
																			+ msg
																			+ "<a onClick='validateHrefVulnerability(this)'  href='dataSourceDisplayAllView?idData="
																			+ $(
																					"#rightsourceid")
																					.val()
																			+ "'	target='_blank'>Click here</a> to make the changes.");
													}
													console
															.log(j_obj.rightsource);
													toastr
															.info(j_obj.rightsource);
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
<script>
	//call for get Second Source Template name
	$(document).ready(function() {

		$.ajax({
			url : './secondSourceTemplateName',
			type : 'GET',

			datatype : 'json',
			//data : form_data,
			success : function(obj) {
				//alert(obj);

				$(obj).each(function(i, item) {

					//	alert("item =>"+item);

					var idAppVal = item.substring(0 , item.indexOf("-"));
					
					
					var TemplateNameVal=item.substring((item.indexOf("-"))+1,item.length);
			
				$('#rightsourceid').append($('<option>', {
					value : idAppVal,
					//text : TemplateNameVal
					//[By Mamta 21 feb 2022]
					text : idAppVal+"_"+TemplateNameVal
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
		//	alert("selectedOption =>"+selectedOption);

		$("#selectedSecondSrcTemplate").val(JSON.stringify(selectedOption));

	});
</script>