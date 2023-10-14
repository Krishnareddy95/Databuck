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
									<span class="caption-subject bold "> Edit Application
										Group </span>
								</div>
							</div>
						</div>
						<br>

						<div class="row">
							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control catch-error" id="nameId"
										name="name" placeholder=" Enter the Name" value="${listAppGroup.name}" readonly="readonly"> 
										<label for="form_control_1">Name*</label>
								</div>
								<br /> <span class="required"></span>
							</div>

							<div class="col-md-6 col-capture">
								<div class="form-group form-md-line-input">
									<input type="text" class="form-control" id="descriptionId" maxlength="200"
										name="description" placeholder=" Enter Description " value="${listAppGroup.description}">
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
									<input type="hidden" id="selectedValidation" name="selectedValidation" />
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
						<input type="hidden" id="idAppGroup" value="${listAppGroup.idAppGroup}" />
						<input type="hidden" id="sel_schedulerId" value="${listAppGroup.idSchedule}" />
						<input type="hidden" id="sel_schedulerEnabledId" value="${listAppGroup.enableScheduling}" />
						<input type="hidden" id="selectedAppIds_id" value="${selectedAppIds}" />
						
						<div class="form-actions noborder align-center">
							<button type="submit" class="btn blue" id="startTaskId">Submit</button>
						</div>
					</div>

					<div class="portlet-body">
						<table
							class="table table-striped table-bordered  table-hover dataTable"
							style="width: 100%;">
							<thead>
								<tr>
									<th>ID</th>
									<th>Validation Name</th>
									<th>Delete</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="appGroupMappingObj" items="${appGroupMappings}">
									<tr>
									<td>${appGroupMappingObj.appId}</td>
									<td>${appGroupMappingObj.appName}</td>
									<td><span style="display: none">${appGroupMappingObj.idAppGroupMapping}</span> <a onClick="validateHrefVulnerability(this)" 
									href="#" class="deleteAppGroupMapping"
									data-toggle="confirmation" data-singleton="true"><i
									style="margin-left: 20%; color: red" class="fa fa-trash"></i></a></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>


			<!-- END SAMPLE FORM PORTLET-->
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />
<!-- END QUICK SIDEBAR -->

<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">

<script>
	//call for get validation name
	$(document).ready(
			function() {
				var enabledSchedule = $('#sel_schedulerEnabledId').val();
				var selectedScheduleId = $('#sel_schedulerId').val();
				
				if(enabledSchedule == 'Y'){
					$('#schedulerEnabledId').prop('checked',true);
					$('#scheduleDivId').removeClass('hidden');
					$("#idSchedulerId option[value='"+selectedScheduleId+"']").attr("selected", "selected");
				} else {
					$('#schedulerEnabledId').prop('checked',false);
					$('#scheduleDivId').addClass('hidden');
					$('#idSchedulerId').val();
				}
	});
</script>	
<script>
	//call for get validation name
	$(document).ready(
			function() {
				var selectedAppIds = $('#selectedAppIds_id').val();
				var s_List = selectedAppIds.split(",");
          	    console.log("selectedAppIds: "+s_List);
          	  	$("#idString").val(selectedAppIds);
				
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
									
									if(s_List.includes(idAppVal)){
									  var x = document.getElementById("appListId").options[id].selected = true;
								    }
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
	
	$('.deleteAppGroupMapping').on('confirmed.bs.confirmation', function () {
		 var table2 = $('.table').DataTable();
         
         var isSingleValidation = table2.rows().count() === 1;

          if (isSingleValidation)
             {
           alert('AppGroupMapping cannot be Deleted');
           return;
       	               
            }
        var idAppGroupMapping = $(this).closest('td').find('span').html();
        var form_data = {
        		idAppGroupMapping : idAppGroupMapping,
            };
             console.log(form_data); 
             $.ajax({
                url: "deleteAppGroupMapping",
                type: 'POST',
                headers: { 'token':$("#token").val()},
                datatype: 'json',
                data: form_data,
                success: function(data) {
                    if (data!="") {
                        toastr.info('AppGroupMapping Deleted Successfully');
                        setTimeout(function(){
                            window.location.reload();
                        },1000); 
                    } else {
                        toastr.info('There was a problem.');
                    }
                },
                error: function(xhr, textStatus, errorThrown) 
                {
                }
            });
        
        return false;
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
						if (schedulerEnabled == "Y" && idScheduler == "") {
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
							idAppGroup : $("#idAppGroup").val(),
							name : $("#nameId").val(),
							description : $("#descriptionId").val(),
							idAppList : $("#idString").val(),
							schedulerEnabled : schedulerEnabled,
							idScheduler : idScheduler

						};
						console.log(form_data);

						$
								.ajax({
									url : './editAppGroup',
									type : 'POST',
									headers: { 'token':$("#token").val()},
									datatype : 'json',
									data : form_data,
									success : function(data) {
										var j_obj = $.parseJSON(data);
										if (j_obj.hasOwnProperty('success')) {
											toastr
													.info('AppGroup edited successfully');
											setTimeout(
													function() {
														location.reload();
														window.location.href = 'viewAppGroups';

													}, 1000);
										} else {
											toastr.info('Unable to edit AppGroup, Please check your configuration and try again.');
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
<script>
	//Mamta 12-May-2022 for enabling pagination of Table 
	$(document).ready(
			function() {
				var table;
				table = $('.table').dataTable({
					  	
					  	order: [[ 0, "desc" ]],
						
				      });
	});
</script>	
<!-- END CONTAINER -->