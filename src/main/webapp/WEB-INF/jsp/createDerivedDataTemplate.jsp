<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<head>

<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">
	
	<link rel="stylesheet"  href="./assets/css/custom-style.css" type="text/css"  /> 

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<style>
</style>

<script>
	var request;
	var vals = "";
	function sendInfo() {
		var v = document.getElementById("datasetid").value;
		var url = "./duplicatedatatemplatename?val=" + v;
		//alert("in ajax code");
		if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}

		try {
			request.onreadystatechange = getInfo;
			request.open("POST", url, true);
			request.setRequestHeader('token',$("#token").val());
			request.send();
		} catch (e) {
		}
	}

	function getInfo() {
		if (request.readyState == 4) {
			var val = request.responseText;
			document.getElementById('amit').innerHTML = val;
			if (val != "") {
				vals = val;
				return false;
			}
		}
	}
</script>


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
							<span class="caption-subject bold "> Create Derived Data
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
					<form id="tokenForm1" action="createDerivedDataTemplate" method="POST" accept-charset="utf-8">
						<div class="portlet-body form">
							<div class="form-body">
								<div class="row" id="template_data">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="datasetid" name="dataset"
												placeholder="Enter the Data Template Name"
												onkeyup="sendInfo()"> <label for="form_control_1">Derived
												Data Template Name *</label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<span id="amit" class="help-block required"></span>
									</div>
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="descriptionid"
												name="description" placeholder="Enter your description">
											<label for="form_control">Description</label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>
								</div>

								<div class="row">
									<input type="hidden" id="template1name" name="template1name"
										value="" /> <input type="hidden" id="template2name"
										name="template2name" value="" />
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control text-left preferenceSelect"
												title="Select one Template" id="template1id" name="template1id">
												<option value="-1" class="hidden">Select Template 1
												</option>

											</select> <label for="form_control_1">Data Template 1 *</label>
											<!-- <span class="help-block">Data Location  </span> -->
										</div>
										<br /> <span class="required"></span>
									</div>

									<div class="col-md-6 col-capture hidden" id="template2_div">
										<div class="form-group form-md-line-input">
											<select class="form-control preferenceSelect2"
												id="template2id" name="template2id" disabled="disabled">

												<option value="-1" class="hidden" id="-1">Select
													Template 2</option>
											</select> <label for="form_control_1">Data Template 2 *</label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control catch-error"
												id="aliasname1id" name="aliasname1" placeholder="Alias name for Data Template 1" value="" readonly>
											<label for="form_control_1">Data Template 1 Alias Name </label>
											<!--  <span class="help-block">Data Set Name </span> -->
										</div>
										<span id="amit" class="help-block required"></span>
									</div>
									<div class="col-md-6 col-capture hidden" id="template2_alias_div">
										<div class="form-group form-md-line-input">
											<input type="text" class="form-control" id="aliasname2id"
												name="aliasname2" placeholder="Alias name for Data Template 2" value="" readonly> 
											<label for="form_control">Data Template 2 Alias Name </label>
											<!-- <span class="help-block">Short text about Data Set</span> -->
										</div>
									</div>

								</div><br>
								<div class="row">
									<div class="md-checkbox">
										<input type="checkbox" class="md-check" id="enableJoinCondition"
											name="enableJoinCondition" value="Y"> <label
											for="enableJoinCondition"><span></span> <span
											class="check"></span> <span class="box"></span>Enable Join Condition</label>
									</div>
								</div><br>

								<div class="row">
									<div class="col-md-12 col-capture">
										<label for="form_control_1">Query * <i>( Please use the alias names generated above in the query )</i></label>
										<div class="textwrapper">
											<textarea style="width: 100%; height: 35%; font-size: 14px;"
												id="querytext" name="querytext"
												placeholder="Enter the query here...."></textarea>
										</div>

									</div>
								</div>
							</div>
						</div>

						<div class="form-actions noborder align-center">

							<button type="submit" id="datasetupid"
								onclick="return validateData()" class="btn blue">
								Create<br />
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
<!-- <script>
        $(document).ready(function() {
        	$('#tokenForm1').ajaxForm({
        		headers: {'token':$("#token").val()}
        	});
        });
 </script> -->

<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<script>
	$(document).ready(function() {
		$('#template1id').multiselect({
			maxHeight : 200,
			buttonWidth : '500px',
			enableFiltering : true,

			nonSelectedText : 'Select Template'

		});

		$('#template2id').multiselect({
			maxHeight : 200,
			buttonWidth : '500px',
			buttonContainer : '<div id="template2id-container">',
			disabledText : 'Disabled ...',
			enableFiltering : true,
			nonSelectedText : 'Select Template'

		});

	});

	$('#enableJoinCondition').click(function() {	
		if ($(this).prop("checked") == true) {
					
			$('#template2_div').removeClass('hidden');
			$('#template2_alias_div').removeClass('hidden');
			
		} else if ($(this).prop("checked") == false) {
			
			$('#template2_div').addClass('hidden');
			$('#template2_alias_div').addClass('hidden');
			
		}
		
		$('#aliasname2id').val('');
		$('#template2name').val('');
		$('#template2id').val(-1);
		
	});

	$(".preferenceSelect").change(
			function() {

				var selectedId = '#' + ($(this).attr('id'));
				var selectedIdVal = $(selectedId).val();
				$('#template2id').removeClass('disabled');
				$('#template2id').multiselect('rebuild');

				$("#template2id option ").each(function() {

					if (selectedIdVal == ($(this).val())) {

						$(this).prop('readonly', true);

					} else {
						$(this).prop('readonly', false);
					}

				});

				$("#template2id-container input").each(
						function() {

							if (selectedIdVal == ($(this).val())) {
								$(this).prop('disabled', true);
								$(this).parent('label').parent('a')
										.parent('li').addClass('disabled');
							} else {
								$(this).parent('label').parent('a')
										.parent('li').removeClass('disabled');
								$(this).prop('disabled', false);
							}
						});

				var aliasname1id = 'derived_' + selectedIdVal + '_source1';
				var template1name = $(this).find(":selected").text();

				if (selectedIdVal != null && selectedIdVal != "-1"
						&& selectedIdVal.trim().length > 0) {
					$('#aliasname1id').val(aliasname1id);
					$('#template1name').val(template1name);

				} else {
					$('#aliasname1id').val('');
					$('#template1name').val('');
				}

			});

	$(".preferenceSelect2").change(
			function() {

				var selectedId = '#' + ($(this).attr('id'));
				var selectedIdVal = $(selectedId).val();
				var aliasname2id = 'derived_' + selectedIdVal + '_source2'

				var template2name = $(this).find(":selected").text();
				if (selectedIdVal != null && selectedIdVal != "-1"
						&& selectedIdVal.trim().length > 0) {

					$('#aliasname2id').val(aliasname2id);
					$('#template2name').val(template2name);

				} else {
					$('#aliasname2id').val('');
					$('#template2name').val('');
				}

			});

	function validateData()

	{

		$('span[id^="spanid"]').remove();

		var no_error = 1;
		
		if(!checkInputText()){ 
			no_error = 0;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}

		var amit1 = $('#amit').html();
		//alert("outside"+amit1)
		console.log(amit1);
		if (amit1 == "") {

		} else {
			console.log("inside if");
			no_error = 0;
		}

		if ($("#datasetid").val().trim().length == 0) {

			$(
					'<span id="spanid" class="input_error" style="font-size:12px;color:red"><i>* Please enter template name </i> </span>')
					.insertAfter($('#datasetid'));
			no_error = 0;
		}

		if ($("#template1id").val() == "-1"
				|| $("#template1id").val().trim().length == 0
				|| $("#template1id").val() == null) {

			$(
					'<span id="spanid" class="input_error" style="font-size:12px;color:red"><i> * Please select a template </i> </span>')
					.insertAfter($('#template1id'));
			no_error = 0;
		}
		
			
	if ($("#enableJoinCondition").prop("checked") == true) {
			if ($("#template2id").val() == "-1"
					|| $("#template2id").val().trim().length == 0
					|| $("#template2id").val() == null) {

				$(
						'<span id="spanid" class="input_error" style="font-size:12px;color:red"><i> * Please select a template </i> </span>')
						.insertAfter($('#template2id'));
				no_error = 0;
			}
		}

		if ($("#template1id").val() == $("#template2id").val()
				&& $("#template1id").val() != "-1") {

			$(
					'<span id="spanid" class="input_error" style="font-size:12px;color:red">Please select different templates</span>')
					.insertAfter($('#template1id'));
			no_error = 0;
		}

		if ($("#querytext").val().trim().length == 0) {

			$(
					'<span id="spanid" class="input_error" style="font-size:14px;color:red"><i>* Query cannot be empty </i> </span>')
					.insertAfter($('#querytext'));
			no_error = 0;
		}

		if (no_error == 0) {
			console.log('error');
			return false;
		}

	}

	$(document).ready(
			function() {

				$.ajax({
					url : './getNonDerivedtemplatename',
					type : 'GET',

					datatype : 'json',
					//data : form_data,
					success : function(obj) {
						//alert(obj);

						$(obj).each(
								function(i, item) {
									var idDataVal = item.substring(0, item
											.indexOf("-"));

									var Tname = item;

									//alert("idData=>"+idDataVal+"TName=>"+TnameVal)
									$('#template1id').append($('<option>', {
										value : idDataVal,
										text : Tname
									}));
									$('#template2id').append($('<option>', {
										value : idDataVal,
										text : Tname
									}));

								});
						$('#template1id').multiselect('rebuild');

					},
					error : function() {

					},

				});
			});
</script>
