
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp" />
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


							<br> <br>
							<div class="row">
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
										<select class="form-control" id="secondsourceid"
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
							</div>
						
						
						<div class="row">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control"
										id="absoluteThresholdId" name="absoluteThresholdId"
										placeholder="Absolute Threshold" value="${matchingThreshold}"> <label
										for="absoluteThresholdId"></label>
								</div>
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
							<button type="submit" id="datamatchgroupcreateid" class="btn blue">Submit</button>
						</div>
					<input type="hidden" value="${DataMatchingGroupCustomize}" id="DataMatchingGroupCustomize">
					<input type="hidden" value="${idRightData}" id="idRightData">
					</div>
				</div>
			</div>
		</div>
	</div>


	<!-- END EXAMPLE TABLE PORTLET-->
</div>
<jsp:include page="footer.jsp" />
<script>
$( document ).ready(function() {
	var DataMatchingGroupCustomize=$("#DataMatchingGroupCustomize").val();
	if(DataMatchingGroupCustomize=="DataMatchingGroupCustomize"){
		var idRightData=$("#idRightData").val();
		$("#secondsourceid").val(idRightData);
	}
    console.log( "ready!" );
});
	//console.log(check);
	$("#datamatchgroupcreateid")
			.click(
					function() {
						var no_error = 1;
						$('.input_error').remove();
						
						if(!checkInputText()){ 
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}

						 if($("#secondsourceid").val()== "-1"){
						    $('<span class="input_error" style="font-size:12px;color:red">Please Select Second Source Template</span>').insertAfter($('#secondsourceid'));
						    no_error = 0;
						} 
						var absoluteThresholdId=$("#absoluteThresholdId").val();
						 if(absoluteThresholdId==""){
							 absoluteThresholdId=0.0
						 }
						if (no_error) {
							var form_data = {
								idApp : $("#idApp").val(),
								idData: $("#idData").val(),
								secondSourceId : $("#secondsourceid").val(),
								absoluteThresholdId :absoluteThresholdId ,
							};
							$('button').prop('disabled', true);
							console.log(form_data); //return false;
							$.ajax({
										url : 'saveDataIntolistApplicationForDataMatchingGroup',
										type : 'POST',
										headers: { 'token':$("#token").val()},
										datatype : 'json',
										  data: form_data,
										//contentType : "application/json",
										success : function(message) {
											console.log(message);
											var j_obj = $.parseJSON(message);
											if (j_obj.hasOwnProperty('success')) {
												//alert(message);
												var DataMatchingGroupCustomize=$("#DataMatchingGroupCustomize").val();
												if(DataMatchingGroupCustomize=="DataMatchingGroupCustomize"){
													toastr.info("Validation Check Customized Successfully");
												}else{
												toastr.info("Validation Check Created Successfully");
												}
												setTimeout(
														function() {
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
										$("#myLink").attr("href", "dataSourceDisplayAllView?idData="+$("#secondsourceid").val());
										console.log(j_obj.secondSource);
										toastr.info(j_obj.secondSource);
										$('#firstSource').addClass(
										'hidden');
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