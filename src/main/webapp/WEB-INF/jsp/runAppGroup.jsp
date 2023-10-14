<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<!-- BEGIN CONTENT -->

<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="init-form portlet-body form">
						<div class="form-body">
							<div class="row"></div>
							<div class="row">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-line-input">
										<label for="form_control_1">Please select the task you
											want to run</label> <input type="hidden" id="idString" value="" />
									</div>
								</div>
							</div>
							<br>

							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control text-left"
											style="text-align: left;" id="idAppGroup_Id"
											name="idAppGroup">
										</select> <label for="form_control">Choose AppGroup* </label> <input
											type="hidden" id="listAppGroupData"
											value="${listAppGroupData}">
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>
							<br>
							<div class="form-actions noborder align-center">
								<button type="submit" class="btn red" id="startTaskId">Submit</button>
							</div>
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
	$(document).ready(
			function() {
				$.ajax({
					url : './getAppGroupNames',
					type : 'GET',
					datatype : 'json',
					success : function(obj) {
						$("#idAppGroup_Id").empty();
		               
						$("<option class='hidden'/>")
		                .attr("value", "-1")
		                .html("Select")
		                .appendTo("#idAppGroup_Id");
		                
						$(obj).each(
								function(i, item) {
									var idAppGroup = item.substring(0, item
											.indexOf("-"));
									var appGroupName = item.substring(0, item.indexOf("-")) 
														+ "_" + item.substring((item.indexOf("-")) + 1, item.length);

									$('#idAppGroup_Id').append($('<option>', {
										value : idAppGroup,
										text : appGroupName
									}));
								});

						$('#idAppGroup_Id').multiselect('rebuild');
					},
					error : function() {
					},
			});
	});

	$('#idAppGroup_Id').multiselect({
		maxHeight : 200,
		buttonWidth : '500px',
		includeSelectAllOption : true,
		enableFiltering : true,
		nonSelectedText : 'Select AppGroup'
	});
	
	$('#startTaskId')
			.click(
					function() {

						//var idAppGroup = $("#idAppGroup_Id").val();

						var no_error = 1;
						$('.input_error').remove();
						if ($("#idAppGroup_Id").val() == -1)  {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please select AppGroup</span>')
									.insertAfter($('#idAppGroup_Id'));
							no_error = 0;
						}

						if (!no_error) {
							return false;
						}

						var form_data = {
							idAppGroup : $("#idAppGroup_Id").val(),
						};
						console.log(form_data);

						$.ajax({
									url : './triggerAppGroup',
									type : 'POST',
									headers: { 'token':$("#token").val()},
									datatype : 'json',
									data : form_data,
									success : function(data) {
										var j_obj = $.parseJSON(data);
										if (j_obj.hasOwnProperty('success')) {
											toastr.info(j_obj['success']);
											setTimeout(function() {
												location.reload();
												window.location.reload();

											}, 1000);
										} else if (j_obj.hasOwnProperty('failure')) {
											toastr.info(j_obj['failure']);
										} else {
											toastr.info('There was a problem.');
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