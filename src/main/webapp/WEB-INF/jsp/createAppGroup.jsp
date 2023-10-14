<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<!-- BEGIN CONTENT -->

<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">

					<input type="hidden" id="timerid" value=1 /> <input type="hidden"
						id="tourl" value="   " />
					<div class="form-body">
						<div class="row">
							<div class="col-md-12 col-capture">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold "> Create Application
										Group </span>
								</div>
							</div>
						</div>
						<br>

						<div class="row">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error" id="nameId" maxlength="45"
										name="name" placeholder=" Enter the Name"> <label
										for="form_control_1">Name*</label>
								</div>
								<br /> <span class="required"></span>
							</div>

							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control" id="descriptionId" maxlength="200"
										name="description" placeholder=" Enter Description ">
									<label for="form_control">Description</label>
								</div>
							</div>
						</div>

						<div class="row">
							<div class="col-md-6 col-capture non-schema-matching">
								<div class="form-group form-md-line-input">
									<select class="form-control text-left"
										style="text-align: left;" id="appListId" multiple="multiple">

									</select> <label for="form_control_1">Choose Validation Check *</label>
									<input type="hidden" id="selectedValidation" name="selectedValidation" value="" />
								</div>
								<br /> <span class="required"></span>
							</div>
							
							<div class="col-md-6 col-capture" id="host-id-container">
								<div class="form-group form-md-line-input">

									<input type="checkbox" class="md-check" id="schedulerEnabledId"
										name="schedulerEnabled" value="Y"> 
								    <label for="form_control_1"
										id="schedulerEnabled">Enable Scheduling</label>
								</div>
								<br /> <span class="required"></span>
							</div>
						</div>
						
						<div class="row hidden" id="scheduleDivId">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<select class="form-control text-left" style="text-align: left;" id="idSchedulerId" 
									name = "idScheduler">
										<option value="-1">Select...</option>
										<c:forEach var="listSchedule" items="${scheduleList}">
									     <option value="${listSchedule.idSchedule}">${listSchedule.name}</option>
									    </c:forEach>
									</select> 
									<label for="form_control">Choose Schedule *</label>
								</div>
								<br /> <span class="required"></span>
							</div>
						</div>

						<input type="hidden" id="idString" value="" /> 

						<div class="form-actions noborder align-center">
							<button type="submit" class="btn blue" id="startTaskId">Submit</button>
						</div>
					</div>


				</div>
			</div>


			<!-- END SAMPLE FORM PORTLET-->
		</div>
	</div>
</div>
<!-- END QUICK SIDEBAR -->
<jsp:include page="footer.jsp" />

<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">

<script>
	//call for get validation name
	$(document).ready(
			function() {

				$.ajax({
					url : './getApprovedValidationsForProject',
					type : 'GET',
					datatype : 'json',
					success : function(obj) {
						$(obj).each(
								function(id, Vname) {
									var idAppVal = Vname.substring(0, Vname
											.indexOf("-"));
									var VnameVal = Vname.substring((Vname
											.indexOf("-")) + 1, Vname.length);
									$('#appListId').append($('<option>', {
										value : idAppVal,
										text : VnameVal
									}));
								});

						$('#appListId').multiselect('rebuild');
						$('#appListId').multiselect({
							includeSelectAllOption : true
						});

					},
					error : function() {

					},

				});
			});
</script>

<script type="text/javascript">
	$(document).ready(function() {
		$('#appListId').multiselect({
			maxHeight : 200,
			buttonWidth : '500px',
			includeSelectAllOption : true,
			enableFiltering : true,
			selectAll : true,
			nonSelectedText : 'Select Validation',
			includeSelectAllOption : true
		});
	});

	var selectedOption;
	$('#appListId').on('change', function() {

		var selectedvalidations = [];
		$('#appListId :selected').each(function(i, selected) {
			selectedvalidations[i] = $(selected).val();
		});

		$("#selectedValidation").val(JSON.stringify(selectedvalidations));
		$("#idString").val(selectedvalidations);

	});
	
	$('#schedulerEnabledId').click(function() {
		if ($(this).prop("checked") == true) {
			$('#scheduleDivId').removeClass('hidden');
		} else if ($(this).prop("checked") == false) {
			$('#scheduleDivId').addClass('hidden');
		}
	});
	
</script>

<script>
	$('#startTaskId')
			.click(
					function() {
						
						var idScheduler= $("#idSchedulerId").val();
						if(idScheduler == -1){
							idScheduler="0";
						}
						
						var schedulerEnabled;
						console.log("schedulerEnabledId checked:"+$("#schedulerEnabledId").prop("checked"));
						if($("#schedulerEnabledId").prop("checked") == true){
							console.log("inside true")
							schedulerEnabled = "Y";
						} else {
							console.log("inside false")
							schedulerEnabled = "N";
							idScheduler="0";
						}
						console.log("schedulerEnabled:"+schedulerEnabled);
						console.log("idScheduler:"+idScheduler);
						
						var no_error = 1;
						$('.input_error').remove();
						
						if(!checkInputText()){ 
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}
						
						if ($("#nameId").val().length == 0) {
							console.log('nameId');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter Name</span>')
									.insertAfter($('#nameId'));
							no_error = 0;
						}
						if ($("#idString").val().length == 0) {
							console.log('appListId');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please select Validation</span>')
									.insertAfter($('#appListId'));
							no_error = 0;
						}
						if (schedulerEnabled == "Y" && idScheduler == "0") {
							console.log('schedulerEnabledId');
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please select schedule</span>')
									.insertAfter($('#idSchedulerId'));
							no_error = 0;
						}
						
						if (!no_error) {
							return false;
						}

						
						var form_data = {
							name : $("#nameId").val(),
							description : $("#descriptionId").val(),
							idAppList : $("#idString").val(),
							schedulerEnabled : schedulerEnabled,
							idScheduler : idScheduler

						};
						console.log(form_data);

						$
								.ajax({
									url : './createAppGroup',
									type : 'POST',
									datatype : 'json',
									headers: {'token':$("#token").val()},
									data : form_data,
									success : function(data) {
										var j_obj = $.parseJSON(data);
										if (j_obj.hasOwnProperty('success')) {
											toastr
													.info('AppGroup created successfully');
											setTimeout(
													function() {
														location.reload();
														window.location.href = 'viewAppGroups';

													}, 1000);
										} else {
											toastr.info(j_obj.fail);
										}
									},
									error : function(xhr, textStatus,
											errorThrown) {
										Success = false;
									}
								});

						return false;
					});
</script>

<!-- END CONTAINER -->