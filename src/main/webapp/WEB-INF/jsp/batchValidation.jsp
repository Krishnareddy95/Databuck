<%@page import="com.databuck.dao.impl.TemplateViewDAOImpl"%>
<%@page import="com.databuck.bean.ListDataSource"%>
<%@page import="java.util.Iterator"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />
<jsp:include page="container.jsp"/>


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
							<span class="caption-subject bold "> Add Batch Validation
								 </span>
						</div>
					</div>
					
					<form id="formAddBatchValidation">
					
					
						<div class="portlet-body form">

							<div class="form-body">
								<div class="row non-schema-matching">
									<!-- <div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="file" class="form-control catch-error"
												id="configPath1" name="configPath1"
												placeholder="Choose Config File"> 
												<label for="form_control_1">Choose Config File *</label>
											 <span class="help-block">Data Set Name </span>
										</div>
										<span id="amit" class="help-block required"></span>
									</div>  -->
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
												<input type="file" class="form-control catch-error" id="configFile" name="configFile" placeholder="Choose Config File"> 
											<label for="form_control_1">Select Config File *</label>
											<span class="help-block">Data Set Name </span>

										</div>
										<span id="amit" class="help-block required"></span>
									</div>
									<div class="col-md-6 col-capture">
									
										<div class="form-group form-md-line-input">
											<select class="form-control" id="type" name="type">
												

													<option value="Dependency">Dependency</option>
													<option value="Null_Check">Null Check</option>
													<option value="Dup_Check">Duplicate Check</option>
													<option value="Matching">Matching</option>

													<option value="RecordCount">Record Count</option>
													<option value="DataMatching">Field to Field Data Matching</option>
                                                    <option value="Q-Null_Check">Quality Null Check</option>
													<option value="Q-Dup_Check">Quality Duplicate Check</option>
													




											</select> <label for="form_control_1">Select Type *</label>
											
										</div>
										<span id="amit1" class="help-block required"></span>
									</div>
								</div>

								<div class="row non-schema-matching hidden" id="isQuality">
									
									<div class="col-md-6 col-capture">
									
										<div class="form-group form-md-line-input">
											<select class="form-control" id="qualityValidation" name="qualityValidation">
												

													<option value="Yes">Yes</option>
													<option value="No">No</option>
													

											</select> <label for="form_control_1">Create Quality Validation *</label>
											
										</div>
										<span id="amit1" class="help-block required"></span>
									</div>
								</div>
								
								
								
								</div>
								<div class="form-actions noborder align-center">
									<button type="submit" id="batch" class="btn blue">Submit</button>
								</div>
								<br> <br>

							</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>




<jsp:include page="footer.jsp" />
<head>
<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">

<script type="text/javascript">

$("#type").change(function(){
	   
	  var sBatchValType = $('#type option:selected').text();
	 
	 if((sBatchValType == "Duplicate Check") || (sBatchValType == "Null Check")){
		
		 $( "#isQuality" ).removeClass( "hidden" );
	 }
	 else{		
		 $( "#isQuality" ).addClass( "hidden" );		
	 }
	});

</script>

<script type="text/javascript">

$(document).ready(function () {

    $("#batch").click(function (oEvent) {        
		oEvent.preventDefault();				//stop default submit of the form
		
		var sBatchValType = $('#type option:selected').text();
		var oForm = $('#formAddBatchValidation')[0], oData = new FormData(oForm); 
		var lIsQuality = ((sBatchValType === "Duplicate Check") || (sBatchValType === "Null Check")) ? $('#qualityValidation option:selected').text() : "NA";

		$("#batch").prop("disabled", true);

		$.ajax({
			type: "POST",
			headers: {'token':$("#token").val()},
			enctype: 'multipart/form-data',
			url: "./createBatchValidation",
			data: oData,
			processData: false,
			contentType: false,
			cache: false,
			timeout: 1000,
			success: function (sResponse) {
				var oResponse = JSON.parse(sResponse);
				toastr.info(oResponse.msg);					
				$("#batch").prop("disabled", false);
			},
			error: function (oError) {
				//console.log("ERROR : ", oError.responseText);
				toastr.info('There was problem');
				$("#batch").prop("disabled", false);
			}
		});
	});
});

</script>

<script>

$('#batch1').click(function() 
	    {
	
	var isQuality = "";
	if((type == "Duplicate Check") || (type == "Null Check")){
		
		 isQuality = $('#qualityValidation option:selected').text();
	 }
	 else{
		
		 isQuality = "NA"; 
		
	 }
	
	        var form_data = {
	        		path: $("#configPath1").val(),
	        		type: $('#type option:selected').val(),
	        		isQuality: isQuality
	            
	        };
	        console.log(form_data); //return false;
	       
	        $.ajax({
	            url: './createBatchValidation',
	            type: 'POST',
	            datatype: 'json',
	            headers: {'token':$("#token").val()},
	            data: form_data,
	            success: function(data) {
	                  if (data!="") {
	                          toastr.info('Trigger Scheduled successfully');
	                        setTimeout(function(){
	                        	location.reload();
	                            window.location.href= 'viewTriggers';

	                        },1000); 
	                      }else {
	                          toastr.info('There was a problem.');
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
</head>
