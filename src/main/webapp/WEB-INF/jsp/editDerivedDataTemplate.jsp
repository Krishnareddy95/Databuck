<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<head>
<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<style>
</style>


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
							<span class="caption-subject bold "> Edit Derived Data
								Template </span>
						</div>
						<div id="loading-progress" class="ajax-loader hidden">
							<h4>
								<b>Table List Loading in Progress..</b>
							</h4>
							<img
								src="${pageContext.request.contextPath}/assets/global/img/loading-circle.gif"
								class="img-responsive" />
						</div>
						<div id="loading-data-progress" class="ajax-loader hidden">
							<h4>
								<b>Table Data Loading in Progress..</b>
							</h4>
							<img
								src="${pageContext.request.contextPath}/assets/global/img/loading-circle.gif"
								class="img-responsive" />
						</div>

					</div>

					<form id="myForm"  action="updateDerivedDataTemplate" method="POST"
						accept-charset="utf-8">
						<div class="portlet-body form">


							<div class="form-body">
								<input type="hidden" name="idDerivedData"
									value="${listDerivedDataSource.idDerivedData }">
								<div class="row" id="template_data">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="datasetid" name="dataset" onkeyup="sendInfo()" value="${listDerivedDataSource.name }" readonly="readonly">
											<label for="form_control_1">Derived Data Template Name *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<span id="amit" class="help-block required"></span>
									</div>
									
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="descriptionid"
												name="description" value="${listDerivedDataSource.description}"> 
											<label for="form_control">Description</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control preferenceSelect" id="template1id" name="template1" disabled="disabled"">
												<option value="${listDerivedDataSource.template1Name}">${listDerivedDataSource.template1Name}
												</option>
											</select> <label for="form_control_1">Data Template 1 *</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>

									<div class="col-md-6 col-capture hidden" id="template2_div">

										<div class="form-group form-md-line-input">
											<select class="form-control preferenceSelect2" id="template2id" name="template2" disabled="disabled">
												<option value="${listDerivedDataSource.template2Name}">${listDerivedDataSource.template2Name}
												</option>
											</select> 
											<label for="form_control_1">Data Template 2 *</label>

										</div>
										<br /> <span class="required"></span>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="aliasname1id" name="aliasname1" readonly="readonly" value="${listDerivedDataSource.template1AliasName}">
											<label for="form_control_1">Data Template 1 Alias Name *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<span id="amit" class="help-block required"></span>
									</div>
									<div class="col-md-6 col-capture hidden" id="template2_alias_div">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="aliasname2id"
												name="aliasname2" readonly="readonly" value="${listDerivedDataSource.template2AliasName}">
											<label for="form_control">Data Template 2 Alias Name *</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								</div><br>
								<div class="row">
									<div class="md-checkbox">
										<input type="checkbox" class="md-check" id="enableJoinCondition"
											name="enableJoinCondition" value="Y" readonly="readonly"> <label
											for="enableJoinCondition"><span></span> <span
											class="check"></span> <span class="box"></span>Enable Join Condition</label>
									</div>
								</div><br>
								<div class="row">
									<div class="col-md-12 col-capture">
										<label for="form_control_1">Enter the query here * <i>(
												Please use the alias names in the query )</i></label>
										<div class="textwrapper">
											<textarea style="width: 100%; height: 35%; font-size: 14px;"
												id="querytext" name="querytext">${listDerivedDataSource.queryText}</textarea>
										</div>
									</div>
								</div>
							</div>
						</div>

						<div class="form-actions noborder align-center">

							<button type="submit" id="datasetupid"
								onclick="return validateData()" class="btn blue">
								Submit<br />
							</button>
						</div>

					</form>
				</div>
			</div>

		</div>

		<!-- END SAMPLE FORM PORTLET-->
	</div>
</div>

<!-- END QUICK SIDEBAR -->
<!-- END CONTAINER -->
<jsp:include page="footer.jsp" />
<head>
<script>
$(document).ready(function() {
	var template2Id = ${listDerivedDataSource.template2IdData};
	
	if (template2Id != null && template2Id > 0) {
		$('#template2_div').removeClass('hidden');
		$('#template2_alias_div').removeClass('hidden');
		$('#enableJoinCondition').prop('checked',true);
	}
    $('#enableJoinCondition').attr("disabled", true);

});

</script>
<script>
function validateData()

{

	$('span[id^="spanid"]').remove();

	var no_error = 1;
	
	if(!checkInputText()){ 
		no_error = 0;
		alert('Vulnerability in submitted data, not allowed to submit!');
	}
	
	if (no_error == 0) {
		console.log('error');
		return false;
	}
}

$('#enableJoinCondition').click(function() {	
	if ($(this).prop("checked") == true) {
				
		$('#template2_div').removeClass('hidden');
		$('#template2_alias_div').removeClass('hidden');
		
	} else if ($(this).prop("checked") == false) {
		
		$('#template2_div').addClass('hidden');
		$('#template2_alias_div').addClass('hidden');
	}
	
});

</script>
<!-- <script> 
        // wait for the DOM to be loaded 
        $(document).ready(function() { 
        	$('#myForm').ajaxForm({
        		headers: {'token':$("#token").val()}
        	}); 
        }); 
 </script> -->

</head>
