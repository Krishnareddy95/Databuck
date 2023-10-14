<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.databuck.bean.ListApplications"%>
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
				
						<div class="form-body">
							<div class="row">
								<div class="col-md-12 col-capture">
									<div class="caption font-red-sunglo">
										<span class="caption-subject bold "> Trigger Task </span>
									</div>
								</div>
							</div>
								<br>

								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select name="Scheduler" id="Schedulerid" class="form-control">
											<option id="select" value="-1">Select</option>
												<c:forEach var="listScheduleDataObj" items="${listScheduleData}">
												<option value=${listScheduleDataObj.key}>${listScheduleDataObj.value}</option>
												</c:forEach>
											</select> <br /> <label for="form_control_1">Choose Scheduler *</label>
										</div>
										<br /> <span class="required"></span>
									</div>
									
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="triggerTypeId" name="triggerType"
												placeholder="Choose TriggerType">
												<option value="-1">Select...</option>
												<option value="validation">Validation</option>
												<option value="schema">Schema</option>
											</select><label for="form_control_1">Trigger Type *</label>
										</div>
									</div>
								</div>
								
								<div class="row hidden" id="validation_div_id">
									<div class="col-md-6 col-capture non-schema-matching">
										<div class="form-group form-md-line-input">
											<select class="form-control text-left"
												style="text-align: left;" id="locationid" multiple="multiple">
												<c:forEach items="${listApplicationsdata}"
													var="listApplicationsobj">
													<option value="${listApplicationsobj.idApp}">${listApplicationsobj.name}</option>
												</c:forEach>

											</select> <label for="form_control_1">Choose Validation Check
												*</label><input type="hidden" id="selectedValidation"
												name="selectedValidation" value="" />
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
								
								<div class="row hidden" id="schema_div_id">
									<div class="col-md-6 col-capture non-schema-matching">
										<div class="form-group form-md-line-input">
											<select class="form-control text-left"
												style="text-align: left;" id="schema_id" multiple="multiple">
												<c:forEach items="${listSchemaData}"
													var="listDataSchemaObj">
													<option value="${listDataSchemaObj.idDataSchema}">${listDataSchemaObj.schemaName}</option>
												</c:forEach>

											</select> <label for="form_control_1">Choose Schema
												*</label><input type="hidden" id="selectedSchema"
												name="selectedSchema" value="" />
										</div>
										<br /> <span class="required"></span>
									</div>
								</div>
								
								<input type="hidden" id="idString" value = "" />
								<input type="hidden" id="idDataSchemaString" value = "" />
								<input type="hidden" id="idAppArray" value = "${idAppArray}" />
								<input type="hidden" id="idDataSchemaArray" value = "${idDataSchemaArray}" />
								<input type="hidden" id="curSchSelectedValidations" value = "" />
								<input type="hidden" id="curSchSelectedSchemas" value = "" />

							<div class="form-actions noborder align-center">
								<button type="submit" class="btn blue" id="trigger_submit_btn_id">Submit</button>
							</div>
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
	
<script type="text/javascript">
	$(document).ready(function() {
		$('#locationid').multiselect({
			maxHeight : 200,
			buttonWidth : '500px',
			includeSelectAllOption : true,
			enableFiltering : true,
			selectAll: true,
			nonSelectedText : 'Select Validation',
			includeSelectAllOption : true
		});
		
		$('#schema_id').multiselect({
			maxHeight : 200,
			buttonWidth : '500px',
			includeSelectAllOption : true,
			enableFiltering : true,
			selectAll: true,
			nonSelectedText : 'Select Schema',
			includeSelectAllOption : true
		});
	});
</script>
<script>

$('#triggerTypeId').on('change', function() {

	var selTriggerType = $('#triggerTypeId').val();
	if(selTriggerType == 'validation'){
		$('#validation_div_id').removeClass('hidden').show();
		$('#schema_div_id').addClass('hidden');
	} 
	else if(selTriggerType == 'schema'){
		$('#validation_div_id').addClass('hidden');
		$('#schema_div_id').removeClass('hidden').show();
	}
	
});

var selectedOption;
$('#locationid').on('change', function() {

	var selectedvalidations = [];
	$('#locationid :selected').each(function(i, selected) {
		selectedvalidations[i] = $(selected).val();
	});

	$("#selectedValidation").val(JSON.stringify(selectedvalidations));
	$("#idString").val(selectedvalidations);
	
});

$('#schema_id').on('change', function() {

	var selectedSchemas = [];
	$('#schema_id :selected').each(function(i, selected) {
		selectedSchemas[i] = $(selected).val();
	});

	$("#selectedSchema").val(JSON.stringify(selectedSchemas));
	$("#idDataSchemaString").val(selectedSchemas);
	
});

$('#Schedulerid').on('change', function() {
	var idApp_Array = $("#idAppArray").val();
	var validationsList = idApp_Array.split(",");
	
	// enable the already disabled for last Schedule
	var lastValidationsList = $("#curSchSelectedValidations").val();
 	  
	 if (lastValidationsList!="") {
   	  var s_oldList = lastValidationsList.split(",");
 	  console.log("LastValidationsList: "+s_oldList);

   	  for (i = 0; i < validationsList.length; i++) {
   		  var v_idApp = validationsList[i];
   		  if(s_oldList.includes(v_idApp)){
       		  var x = document.getElementById("locationid").options[i].disabled = false;
       	  }
   	  }
   	  $('#locationid').multiselect('rebuild');
   	  $('#curSchSelectedValidations').val('');
     }
	 
	 
	var idDataSchema_Array = $("#idDataSchemaArray").val();
    var schemasList = idDataSchema_Array.split(",");
		
	// enable the already disabled for last Schedule
	var lastSchemasList = $("#curSchSelectedSchemas").val();
 	  
	 if (lastSchemasList!="") {
	   	  var s_schemaOldList = lastSchemasList.split(",");
	 	  console.log("LastSchemasList: "+s_schemaOldList);
	
	   	  for (i = 0; i < schemasList.length; i++) {
	   		  var s_idDataSchema = schemasList[i];
	   		  if(s_schemaOldList.includes(s_idDataSchema)){
	       		  var x = document.getElementById("schema_id").options[i].disabled = false;
	       	  }
	   	  }
	   	  $('#schema_id').multiselect('rebuild');
	   	  $('#curSchSelectedSchemas').val('');
     }
	 
	 // Prepare the request
	var idSch = $("#Schedulerid").val();
	var form_data = {
    		idScheduler: $("#Schedulerid").val()
        
    };
   
    $.ajax({
        url: './getValditionsForSchedule?idScheduler='+idSch,
        type: 'GET',
        datatype: 'json',
        data: form_data,
        success: function(selectedValidationsList) {
              if (selectedValidationsList!="") {
            	  var s_List = selectedValidationsList.split(",");
            	  console.log("selectedValidationsList: "+s_List);
            	  
            	  for (i = 0; i < validationsList.length; i++) {
            		  var v_idApp = validationsList[i];
            		  if(s_List.includes(v_idApp)){
            			  var x = document.getElementById("locationid").options[i].disabled = true;
                	  } 
            	  }
            	  $('#locationid').multiselect('rebuild');
            	  $('#curSchSelectedValidations').val(selectedValidationsList);
              }
          },
          error: function(xhr, textStatus, errorThrown){
                $('#initial').hide();
                $('#fail').show();
          }
       });
    
    $.ajax({
        url: './getSchemasForSchedule?idScheduler='+idSch,
        type: 'GET',
        datatype: 'json',
        data: form_data,
        success: function(selectedSchemasList) {
              if (selectedSchemasList!="") {
            	  var s_List = selectedSchemasList.split(",");
            	  console.log("selectedSchemasList: "+s_List);
            	  
            	  for (i = 0; i < schemasList.length; i++) {
            		  var s_idDataSchema = schemasList[i];
            		  if(s_List.includes(s_idDataSchema)){
            			  var x = document.getElementById("schema_id").options[i].disabled = true;
                	  } 
            	  }
            	  $('#schema_id').multiselect('rebuild');
            	  $('#curSchSelectedSchemas').val(selectedValidationsList);
              }
          },
          error: function(xhr, textStatus, errorThrown){
                $('#initial').hide();
                $('#fail').show();
          }
       });
    
    return false;
}); 

$('#trigger_submit_btn_id').click(function() {
	var selTriggerType = $('#triggerTypeId').val();
	 
	 

	var no_error=1;
	 $('.input_error').remove();
	 console.log('Schedulerid'+$("#Schedulerid").val());
	 console.log('triggerTypeId'+$("#triggerTypeId").val());
	 console.log('idString'+$("#idString").val());
	 console.log('idDataSchemaString'+$("#idDataSchemaString").val());
	  
	 
	 if($("#Schedulerid").val()== -1) {
	 		console.log('Schedulerid');
	 		  $('<span class="input_error" style="font-size:12px;color:red">Please Choose a Scheduler</span>').insertAfter($('#Schedulerid'));
	 	        no_error = 0;
	 }
	 
	 if($("#triggerTypeId").val()== -1) {
	 		console.log('triggerTypeId');
	 		  $('<span class="input_error" style="font-size:12px;color:red">Please Choose Trigger Type</span>').insertAfter($('#triggerTypeId'));
	 	        no_error = 0;
	 }
	 
	 if(selTriggerType == 'validation'){
		 if($("#idString").val() == '') {
		 		console.log('locationid');
		 		  $('<span class="input_error" style="font-size:12px;color:red">Please Choose Validaion Check</span>').insertAfter($('#locationid'));
		 	        no_error = 0;
		 	}
	 } else if(selTriggerType == 'schema'){
		 if($("#idDataSchemaString").val() == '') {
		 		console.log('schema_id');
		 		  $('<span class="input_error" style="font-size:12px;color:red">Please Choose Schema</span>').insertAfter($('#schema_id'));
		 	        no_error = 0;
		 	}
	 }
 	
 	
 if(! no_error){
           return false;
       }
	        var form_data = {
	        		idApp: $("#idString").val(),
	        		idDataSchema: $("#idDataSchemaString").val(),
	        		idScheduler: $("#Schedulerid").val(),
	            
	        };
	        console.log(form_data); //return false;
	        $('button').prop('disabled', true);
	        $.ajax({
	            url: './triggerTaskSchedule',
	            type: 'POST',
	            headers: { 'token':$("#token").val()},
	            datatype: 'json',
	            data: form_data,
	            success: function(data) {
	                  if (data!="") {
	                          toastr.info('Trigger Scheduled successfully');
	                        setTimeout(function(){
	                        	//location.reload();
	                            window.location.href= 'viewTriggers';
	                            
	                        },1000); 
	                      }else {
	                          toastr.info('There was a problem.');
	                          $('button').prop('disabled', false);
	                      }
	                  },
	              error: function(xhr, textStatus, errorThrown){
	                    $('#initial').hide();
	                    $('#fail').show();
	              }
	           });
	        
	        return false;
	    }); 
</script>
<!-- END CONTAINER -->