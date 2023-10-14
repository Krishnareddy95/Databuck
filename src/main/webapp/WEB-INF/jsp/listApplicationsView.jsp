<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<!-- BEGIN CONTENT -->
<head>

<style type="text/css">
    .multiselect-container {
        width: 500px !important;
    }
</style>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- <link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script> -->
</head>
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">

					<div
						class="init-form portlet-body form <c:if test="${scheduledTask eq 'yes' }">  hidden </c:if> ">
						<form id="tokenForm1" role="form" method="post" action="runTaskResult">
							<input type="hidden" id="timerid" value=1 />

							<input type="hidden" id="isRuleCatalogDiscovery" value="${isRuleCatalogDiscovery}" />
							<div class="form-body">
								<div class="row">
									<div class="col-md-12 col-capture">
										<div class="form-group form-md-line-input">
											<label for="form_control_1">Please select the task
												you want to run</label>
												<input type="hidden" id="idString" value = "" />
												<input type="hidden" id="uniqueId" value = "" />
										</div>
									</div>
								</div>
								<br />

								<%-- 		<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select name="idApp" id="locationid" class="form-control">
												
												<c:forEach items="${listApplicationsdata}"
													var="listApplicationsobj">
													<option value="${listApplicationsobj.idApp}">${listApplicationsobj.name}</option>
												</c:forEach>
											</select> <br /> <label for="form_control_1">Choose
												Validation Check</label>
										</div>
										<br /> <span class="required"></span>
									</div>
								</div> --%>
								<div class="row">
									<div class="col-md-6 col-capture non-schema-matching">
										<div class="form-group form-md-line-input">
											<select class="form-control text-left"
												style="text-align: left;" id="locationid" multiple="multiple">



											</select> <label for="form_control_1">Choose Validation Check
												*</label><input type="hidden" id="selectedValidation"
												name="selectedValidation" value="" />


										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 col-capture <c:if test="${isRuleCatalogDiscovery eq 'N' }">  hidden </c:if> ">
                                        <div class="form-group form-md-line-input">
                                            <select class="form-control" id="validationRunTypeId" name="validationRunType" style="width: 500px;">
                                                <c:forEach items="${validationRunTypeMap}" var="runType">
                                                        <option value="${runType.key}">${runType.value}</option>
                                                </c:forEach>
                                            </select>
                                            <label for="form_control_1">Validation Run Type</label>
                                        </div>
                                    </div>
								</div>

							</div>

							<div class="form-actions noborder align-left">
								<button type="submit" class="btn red" id="startTaskId">submit</button>

							</div>
						</form>
					</div>

					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold " id="appNameId"> </span>
						</div>
					</div>

					<div
						class="note note-info task-in-progress  <c:if test="${scheduledTask ne 'yes' }">  hidden </c:if> ">
						Task in progress <i class="fa fa-spinner fa-spin"></i>
					</div>

					<div class="note note-info task-completed hidden">
						<!-- Task completed <i class="fa fa-check"></i> -->
						Task queued <i class="fa fa-check"></i>
					</div>

					<div class="progress hidden" id="percentagebar">
						<div class="progress">
							<div class="progress-bar" role="progressbar" style="width: 5%;"
								aria-valuenow="100" aria-valuemin="0" aria-valuemax="100"
								id="progressbar">5%</div>
						</div>
					</div>
					<div class="note note-info task-failed hidden">Task could not
						be completed !</div>
				</div>

				<!-- END SAMPLE FORM PORTLET-->
			</div>
		</div>
	</div>
</div>
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
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">
<script type="text/javascript">
	$(document).ready(function() {
		//$('#lstTable').multiselect();
		$('#locationid').multiselect({
			maxHeight : 200,
			buttonWidth : '500px',
			includeSelectAllOption : true,
			enableFiltering : true,
			selectAll: true,
			nonSelectedText : 'Select Validation'

		});
	});
</script>
<script>
	var selectedOption;
	$('#locationid').on('change', function() {

		//alert("in chng");

	/* 	var currentSelection;

		var currentValues = $(this).val();
		if (currentValues.length == 1) {
			currentSelection = currentValues;
		}
		selectedOption = $(this).val();
	//	alert("selectedOption =>"+selectedOption);

		$("#selectedValidation").val(JSON.stringify(selectedOption)); */
		
		var selectedvalidations = [];
		$('#locationid :selected').each(function(i, selected) {
			selectedvalidations[i] = $(selected).val();
			//alert("Selected=> "+selectedvalidations[i]);
		});

		$("#selectedValidation").val(JSON.stringify(selectedvalidations));
		//alert("Selected=> "+selectedvalidations);
		
 
		$("#idString").val(selectedvalidations);
		
	});

	/* Pradeep 4-Apr-2021 Buggy code corrected here and controller both places.  TBD with Dutta and Sreelakhsmi appending idApp to name ok or not? */
	$(document).ready(
			function() {

				$.ajax({
					url : './validationCheckName',
					type : 'GET',
					datatype : 'json',
					success : function(aApplicationNames) {
						$(aApplicationNames).each(
								function(nIndex, sApplicationName) {
									var aNameParts = sApplicationName.split('_'), sIdApp = '', sAppName = '';
									
									console.log('01 got input name as ** ' + sApplicationName);
									
									sAppName = sApplicationName;
									
									// first part surely id app, but for name use substring as there may be multiple '_' chars in name
									if (aNameParts.length > 1) {                             
										sIdApp = aNameParts[0];										
								
									// Controller is appending '<idApp>_' in name, this is just safe boundary code if '_' missing then value = sApplicationName	
									} else if (aNameParts.length === 1) {
										sIdApp = sApplicationName;
									}
									
									console.log('02 Setting dropdown option as sIdApp **' + sIdApp + '** ' + 'sAppName = **' +  sAppName);

									$('#locationid').append($('<option>', {
										value : sIdApp,
										text : sAppName
									}));
								});

						$('#locationid').multiselect('rebuild');
						$('#locationid').multiselect({
							includeSelectAllOption : true
						});

					},
					error : function() {

					}
				});
			});
</script>
	<script>
	$('#startTaskId')
			.click(
					function() {
						
						var no_error = 1;
						$('.input_error').remove();
						
						// Check if validations are selected
						if ($("#idString").val().length == 0) {
							$('<span class="input_error" style="font-size:12px;color:red">Please select atleast one validation</span>')
								.insertAfter($('#locationid'));
							no_error = 0;
						}
						/* var form_data = {
						     appid: $("#schTaskId").val(),
						     times: new Date().getTime()
						 };*/
						// alert($("#idString").val());
						 //alert("locationid =>"+$("#locationid").val());
					if(no_error){
						var form_data = {
							//idApp : $("#locationid").val(),
							idApp1 : $("#idString").val(),
							task_name : $("#locationid option:selected").text(),
							validationRunType : $("#validationRunTypeId").val()
						// value : $('#locationid:selected').html(),
						// times: new Date().getTime(),

						};
					//	alert("idApp =>"+$("#idString").val());
						
						//console.log(form_data); return false;
						$('.init-form').addClass('hidden').hide();
						$('.task-in-progress').removeClass('hidden').show();
						console.log($("form").attr('action'));
						$
								.ajax({
									url : 'runTaskResult',
									type : 'POST',
									datatype : 'json',
									headers: { 'token':$("#token").val()},
									data : form_data,
									success : function(message) {
										var j_obj = $.parseJSON(message);
										
									//	alert("j_obj ==>"+j_obj);
										
										var appName = j_obj.appName;
										$('#appNameId')
												.html(
														"Validation Check : "
																+ appName);

										var uniqueId = j_obj.uniqueId;
										$("#uniqueId").val(uniqueId);
										
										var form_data1 = {
												idApp1 : $("#idString").val(),
												task_name : $("#locationid option:selected").text(),
												uniqueId : $("#uniqueId").val()
										};
										// if (j_obj.hasOwnProperty('success')) {
										$('.task-completed').removeClass(
												'hidden').show();
										$('.task-in-progress').addClass(
												'hidden').hide();
										var myInterval = setInterval(
												function() {
													if ($("#timerid").length > 0) {

														//callAjaxFunc to check the current status of task-in-progress
														$
																.ajax({
																	url : 'statusPoll',
																	type : 'POST',
																	datatype : 'json',
																	headers: { 'token':$("#token").val()},
																	data : form_data1,
																	success : function(
																			message1) {
																		var j_obj1 = $
																				.parseJSON(message1);
																		
																		//alert("j_obj1 ==>"+j_obj1);

																		console
																				.log(j_obj1);//return false;
																		if (j_obj1
																				.hasOwnProperty('success')) {
																			$(
																					'.task-completed')
																					.html(
																							j_obj1.success);
																			//
																			if (j_obj1.success == "Task in progress") {
																				var percentage = j_obj1.percentage;
																			//	alert("j_obj1.percentage =>"+j_obj1.percentage);
																				
																				console
																						.log(percentage);
																				$(
																						'#percentagebar')
																						.removeClass(
																								'hidden')
																						.show();
																				$(
																						"#progressbar")
																						.css(
																								"width",
																								percentage
																										+ '%');
																				$(
																						'#progressbar')
																						.html(
																								percentage
																										+ '%');
																			}

																			console
																					.log(j_obj1.success);
																			if (j_obj1.success == 'Task completed') {
																				$(
																						'#percentagebar')
																						.addClass(
																								'hidden');
																				clearInterval(myInterval);
																			}
																			if (j_obj1.success == 'Task failed' || j_obj1.success == 'Task killed') {
																				$(
																						'#percentagebar')
																						.addClass(
																								'hidden');
																				clearInterval(myInterval);
																			}
																		}
																		if (j_obj1
																				.hasOwnProperty('fail')) {
																			$(
																					'#percentagebar')
																					.addClass(
																							'hidden');
																			console
																					.log(j_obj1.fail);
																		}
																	},
																	error : function(
																			xhr,
																			textStatus,
																			errorThrown) {
																		//$('.validation-fail').removeClass('hidden').html("There seems to be a network problem. Please try again in some time.");
																		console
																				.log(errorThrown);
																	}
																});
														return false;
													} else {
														clearInterval(myInterval);
														$('#percentagebar')
																.addClass(
																		'hidden');
													}
												}, 1500);
										/*  } else {
										      if (j_obj.hasOwnProperty('fail')) {
										          $('.task-failed').removeClass('hidden').show();
										          alert("else part")
										          $('.task-in-progress').addClass('hidden').hide();
										      }
										  }*/
									},
									error : function(xhr, textStatus,
											errorThrown) {
										console.log("failed1");
										$('.validation-fail')
												.removeClass('hidden')
												.html(
														"There seems to be a network problem. Please try again in some time.");
									}
								});
						}
						return false;
					});
</script>