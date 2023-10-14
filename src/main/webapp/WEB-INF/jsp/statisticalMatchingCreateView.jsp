
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
							value="${description}" />
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

							<!--  <div class="form-actions noborder align-center">
                                        <button type="submit" id="appcreateid" class="btn blue">Submit</button>
                                    </div> -->
							<br> <br>
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control" id="descriptionid"
										name="description" value="${applicationName}" readonly /> <label
										for="form_control">First Source Template </label>
									<!-- <span class="help-block">Short text about Data Set</span> -->
								</div>
							</div>

							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<select class="form-control" id="rightsourceid"
										name="rightsource" placeholder="">
										<option value="-1">Select Second Source Template</option>
										<c:forEach var="getlistdatasourcesnameobj"
											items="${getlistdatasourcesname}">
											<option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.name}</option>
										</c:forEach>
									</select> <label for="form_control_1">Second Source Template *</label>
								</div>
								<br /> <span class="required"></span>
							</div>
						</div>

						<%-- <div class="col-wrapper">
							<span style="display: none" class="formid">leftsiderule</span>
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control functionslist" name="leftfunction"
											placeholder="">
											<option value="-1">Select Function</option>
											<c:forEach var="listRefFunctionsname"
												items="${listRefFunctionsname}">
												<option value="${listRefFunctionsname.name}">${listRefFunctionsname.name}</option>
											</c:forEach>
										</select> <label for="leftfunid">Select Function *</label>
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
										</select> <label for="form_control_1">Select Function*</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>
						</div> --%>
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
									</select> <label for="columnid">Select Left Data Column *</label><br>
								</div>
							</div>

							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<select class="form-control" id="rightitemid" name="rightitem">
									</select> <label for="form_control_1">Select Right Data Column *</label>
								</div>
								<br /> <span class="required"></span>
							</div>
						</div>

						<!-- <div class="form-body" id="s4_form_id">
							<div class="row">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="matchkeyfid" name="matchkeyformula" value="" /> <label
											for="form_control_1">Rule</label>
										 <span class="help-block">Data Set Name </span>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>
						</div> -->

						<div class="form-body" id="statisticalDivId">
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-3" style="padding-top: 10px;">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<!-- <input type="checkbox" id="recordCount" name="recordCount"
														class="md-check" value="Y"> <label
														for="recordCount"> Record Count<span></span> <span
														class="check"></span> <span class="box"></span>
													</label> --><label>Record Count</label>
												</div>
											</div>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group form-md-line-input">
											<select name="recordCountType" id="recordCountType"
												class="form-control recordCountType">
												<option value="Percentage">Percentage</option>
												<option value="Absolute Difference">Absolute
													Difference</option>
											</select>
										</div>
									</div>
									<div class="col-md-5">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
												id="recordCountThreshold" name="recordCountThreshold"
												placeholder="Threshold"> <label
												for="recordCountThreshold"></label>
										</div>
									</div>
								</div>
							</div>

							<div class="row">
								<div class="col-md-12">
									<div class="col-md-3" style="padding-top: 10px;">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="measurementSum"
														name="measurementSum" class="md-check" value="Y">
													<label for="measurementSum"> Measurement Sum<span></span>
														<span class="check"></span> <span class="box"></span>
													</label>
												</div>
											</div>
										</div>
									</div>
									<div class="hidden" id="measurementSumDiv">
									<div class="col-md-4">
										<div class="form-group form-md-line-input">
											<select name="measurementSumType" id="measurementSumType"
												class="form-control">
												<option value="Percentage">Percentage</option>
												<option value="Absolute Difference">Absolute
													Difference</option>
											</select>
										</div>
									</div>
									<div class="col-md-5">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
												id="measurementSumThreshold" name="measurementSumThreshold"
												placeholder="Threshold"> <label
												for="measurementSumThreshold"></label>
										</div>
									</div>
									</div>
								</div>
							</div>

							<div class="row">
								<div class="col-md-12">
									<div class="col-md-3" style="padding-top: 10px;">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="measurementMean"
														name="measurementMean" class="md-check" value="Y">
													<label for="measurementMean"> Measurement Mean<span></span>
														<span class="check"></span> <span class="box"></span>
													</label>
												</div>
											</div>
										</div>
									</div>
									<div class="hidden" id="measurementMeanDiv">
									<div class="col-md-4">
										<div class="form-group form-md-line-input">
											<select name="measurementMeanType" id="measurementMeanType"
												class="form-control">
												<option value="Percentage">Percentage</option>
												<option value="Absolute Difference">Absolute
													Difference</option>
											</select>
										</div>
									</div>
									<div class="col-md-5">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
												id="measurementMeanThreshold"
												name="measurementMeanThreshold" placeholder="Threshold">
											<label for="measurementMeanThreshold"></label>
										</div>
									</div>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-3" style="padding-top: 10px;">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="measurementStdDev"
														name="measurementStdDev" class="md-check" value="Y">
													<label for="measurementStdDev"> Measurement Std Dev<span></span>
														<span class="check"></span> <span class="box"></span>
													</label>
												</div>
											</div>
										</div>
									</div>
									<div class="hidden" id="measurementStdDevDiv">
									<div class="col-md-4">
										<div class="form-group form-md-line-input">
											<select name="measurementStdDevType"
												id="measurementStdDevType" class="form-control">
												<option value="Percentage">Percentage</option>
												<option value="Absolute Difference">Absolute
													Difference</option>
											</select>
										</div>
									</div>
									<div class="col-md-5">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control"
												id="measurementStdDevThreshold"
												name="measurementStdDevThreshold" placeholder="Threshold">
											<label for="measurementStdDevThreshold"></label>
										</div>
									</div>
								</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-3" style="padding-top: 10px;">
										<div class="form-group form-md-checkboxes">
											<div class="md-checkbox-list">
												<div class="md-checkbox">
													<input type="checkbox" id="groupBy" name="groupBy"
														class="md-check" value="Y"> <label for="groupBy">
														Group By<span></span> <span class="check"></span> <span
														class="box"></span>
													</label>
												</div>
											</div>
										</div>
									</div>
									<!-- <div class="col-md-4">
										<div class="form-group form-md-line-input">
											<select name="groupByType" id="groupByType"
												class="form-control">
												<option value="Percentage">Percentage</option>
												<option value="Absolute Difference">Absolute
													Difference</option>
											</select>
										</div>
									</div>
									<div class="col-md-5">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="groupByThreshold"
												name="groupByThreshold" placeholder="Threshold"> <label
												for="groupByThreshold"></label>
										</div>
									</div> -->
								</div>
							</div>

						</div>
						<div id="firstSource" class="hidden ">
						<div class="alert alert-warning">
						<h5 style="color:red" id="jsonmsgforderivedcols"></h5>
							<h5 style="color:red">
								&nbsp&nbsp&nbsp&nbsp Please make sure that you have selected atleast one dgroup from the selected data template. 
								<a onClick="validateHrefVulnerability(this)" 	href="dataSourceDisplayAllView?idData=${idData}"
									target="_blank">Click here</a> to make the changes.
							</h5>
						</div>
						</div>
						<div id="secondSource" class="hidden ">
						<div class="alert alert-warning">
						<h5 style="color:red" id="jsonmsgforderivedcols"></h5>
							<h5 style="color:red">
								&nbsp&nbsp&nbsp&nbsp Please make sure that you have selected atleast one dgroup from the selected data template. 
								<a onClick="validateHrefVulnerability(this)" 	href="dataSourceDisplayAllView?idData=${idData}"
									target="_blank" id="myLink">Click here</a> to make the changes.
							</h5>
						</div>
						</div>

						<div class="form-actions noborder align-center tab-one-save">
							<button type="submit" id="matchkeycreateid" class="btn blue">Save</button>
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>


	<!-- END EXAMPLE TABLE PORTLET-->
</div>
<jsp:include page="footer.jsp" />
<script>
$('#measurementSum').click(function() {
	if ($(this).is(':checked')) {
		// alert("Are you sure?");
		$('#measurementSumDiv').removeClass('hidden').show();
	} else {
		 $('#measurementSumType').prop('selectedIndex',0);
		 $('#measurementSumThreshold').val("");
		$('#measurementSumDiv').addClass('hidden');
	}
});
$('#measurementStdDev').click(function() {
	if ($(this).is(':checked')) {
		// alert("Are you sure?");
		$('#measurementStdDevDiv').removeClass('hidden').show();
	} else {
		 $('#measurementStdDevType').prop('selectedIndex',0);
		 $('#measurementStdDevThreshold').val("");
		$('#measurementStdDevDiv').addClass('hidden');
	}
});
$('#measurementMean').click(function() {
	if ($(this).is(':checked')) {
		// alert("Are you sure?");
		$('#measurementMeanDiv').removeClass('hidden').show();
	} else {
		 $('#measurementMeanType').prop('selectedIndex',0);
		 $('#measurementMeanThreshold').val("");
		$('#measurementMeanDiv').addClass('hidden');
	}
});

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
	//console.log(check);
	$("#matchkeycreateid").click(function() {
		var no_error = 1;
		$('.input_error').remove();
		
		if(!checkInputText()){ 
			no_error = 0;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}

		 if($("#rightsourceid").val()== '-1'){
		    $('<span class="input_error" style="font-size:12px;color:red">Please Choose Second Source Template</span>').insertAfter($('#rightsourceid'));
		    no_error = 0;
		} 
		  if($("#leftitemid").val()== '-1'){
			    $('<span class="input_error" style="font-size:12px;color:red">Please Choose a Data Column</span>').insertAfter($('#leftitemid'));
			    no_error = 0;
			} 
		 if($("#rightitemid").val()== '-1'){
			    $('<span class="input_error" style="font-size:12px;color:red">Please Choose a Data Column</span>').insertAfter($('#rightitemid'));
			    no_error = 0;
			}  
		
		if (no_error) {

			var measurementSum = "";
			if ($('#measurementSum').is(":checked")) {
				measurementSum = "Y";
			} else {
				measurementSum = "N";
			}
			var measurementMean = "";
			if ($('#measurementMean').is(":checked")) {
				measurementMean = "Y";
			} else {
				measurementMean = "N";
			}
			var measurementStdDev = "";
			if ($('#measurementStdDev').is(":checked")) {
				measurementStdDev = "Y";
			} else {
				measurementStdDev = "N";
			}
			var groupBy = "";
			if ($('#groupBy').is(":checked")) {
				groupBy = "Y";
			} else {
				groupBy = "N";
			}

			var listStatisticalMatchingConfig = {
				idApp : $("#idApp").val(),
				recordCountType :  $("#recordCountType").val(),
				recordCountThreshold :  $("#recordCountThreshold").val(),
				measurementSum : measurementSum,
				measurementSumType :  $("#measurementSumType").val(),
				measurementSumThreshold :  $("#measurementSumThreshold").val(),
				measurementMean :  measurementMean,
				measurementMeanType :  $("#measurementMeanType").val(),
				measurementMeanThreshold :  $("#measurementMeanThreshold").val(),
				measurementStdDev :  measurementStdDev,
				measurementStdDevType :  $("#measurementStdDevType").val(),
				measurementStdDevThreshold :  $("#measurementStdDevThreshold").val(),
				groupBy :  groupBy,
				//groupByType :  $("#groupByType").val(),
				//groupByThreshold :  $("#groupByThreshold").val(),
				leftSourceId : $("#idData").val(),
				rightSourceId : $("#rightsourceid").val(),
				leftSideExp:$("#leftitemid").val(),
				rightSideExp:$("#rightitemid").val()
				//expression : $("#matchkeyfid").val()
			};
			$('button').prop('disabled', true);
			console.log(listStatisticalMatchingConfig); //return false;
			$.ajax({
				url : 'saveDataIntolistStatisticalMatchingConfig',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				datatype : 'json',
				data : JSON.stringify(listStatisticalMatchingConfig),
				 contentType:"application/json",
				success : function(message) {
					console.log(message);
					var j_obj = $.parseJSON(message);
					if (j_obj.hasOwnProperty('success')) {
						//alert(message);
						toastr.info("Match Key created successfully");
						setTimeout(function() {
							window.location.href = 'validationCheck_View';
							//window.location.reload();

						}, 1000);
					} else if (j_obj.hasOwnProperty('firstSource')) {
						$('button').prop('disabled', false);
						$('#firstSource').removeClass(
						'hidden').show();
				var msg = j_obj['firstSource']
				console.log(msg);
				
				console.log(j_obj.firstSource);
				toastr.info(j_obj.firstSource);
				$('#secondSource').addClass(
				'hidden');
				
					} else if (j_obj.hasOwnProperty('secondSource')) {
						$('button').prop('disabled', false);
						$('#secondSource').removeClass(
						'hidden').show();
				var msg = j_obj['secondSource']
				console.log(msg);
				$("#myLink").attr("href", "dataSourceDisplayAllView?idData="+ $("#rightsourceid").val());
				console.log(j_obj.secondSource);
				toastr.info(j_obj.secondSource);
				$('#firstSource').addClass(
				'hidden');
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
</script>