<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />

<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-12" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> QuickStart Progress -
								<span style="color: #337ab7;">${qs_title}</span>
							</span>
						</div>
					</div>

					<div class="portlet light bordered init">
						<div class="portlet-body">
							<div class="portlet-title">
								<div class="caption font-red-sunglo hidden" id="src_fileDwnldSuccessDiv_id">
									<span class="caption-subject bold "> Source File download to
										DATABUCK_HOME is successful !! </span>

									<div class="portlet-body" style="color: #337ab7;">${src_file_path}</div>
								</div>
								
								<div class="caption font-red-sunglo hidden" id="src_fileDwnldFailDiv_id">
									<span class="caption-subject bold "> Source File download to
										DATABUCK_HOME is failed !! </span>

									<div class="portlet-body" style="color: #337ab7;">${srcFileDownloadMessage}</div>
								</div>
							</div>
						</div>
					</div>
					
					<div class="portlet light bordered init">
						<div class="portlet-body">
							<div class="portlet-title">
								<div class="caption font-red-sunglo hidden" id="trgt_fileDwnldSuccessDiv_id">
									<span class="caption-subject bold "> Target File download to
										DATABUCK_HOME is successful !! </span>

									<div class="portlet-body" style="color: #337ab7;">${trgt_file_path}</div>
								</div>
								
								<div class="caption font-red-sunglo hidden" id="trgt_fileDwnldFailDiv_id">
									<span class="caption-subject bold "> Target File download to
										DATABUCK_HOME is failed !! </span>

									<div class="portlet-body" style="color: #337ab7;">${trgtFileDownloadMessage}</div>
								</div>
							</div>
						</div>
					</div>
					
					<!-- Source Template creation -->
					<div id="qs_src_templateStage" class="portlet light bordered init">
						<div class="portlet-body">
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold " id="src_templateName_Id">
										Source Template creation - <span style="color: #337ab7;">
										<a onClick="validateHrefVulnerability(this)"  href="listdataview?idData=${srcTemplateId}&dataLocation=FileSystem&name=${srcTemplateName}&description=${srcTemplateDescription}">
											${srcTemplateName}</a></span>
									</span> 
									
									<input type="hidden" id="src_template_id" name="srcTemplateId" value="${srcTemplateId}" /> 
									<input type="hidden" id="src_template_name" name="srcTemplateName" value="${srcTemplateName}" /> 
									<input type="hidden" id="src_template_uniqueId" name="srcTemplateUniqueId" value="${srcTemplateUniqueId}" /> 
									<input type="hidden" id="src_templ_timerid" value=1 />
									<input type="hidden" id="src_fileDownloadStatus_id" value="${srcFileDownloadStatus}" />
									<input type="hidden" id="trgt_fileDownloadStatus_id" value="${trgtFileDownloadStatus}" />
								</div>
							</div>

							<div class="portlet-body">
								<div class="note note-info task-in-progress  hidden"
									id="qs_src_templ_task_in_progress">
									Task in progress <i class="fa fa-spinner fa-spin"></i>
								</div>

								<div class="note note-info task-completed hidden"
									id="qs_src_templ_task_completed">
									Task queued <i class="fa fa-check"></i>
								</div>

								<div class="progress hidden" id="qs_src_templ_percentagebar">
									<div class="progress">
										<div class="progress-bar" role="progressbar"
											style="width: 5%;" aria-valuenow="100" aria-valuemin="0"
											aria-valuemax="100" id="qs_src_templ_progressbar">5%</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					
					<!-- Target Template creation -->
					<div id="qs_trgt_templateStage" class="portlet light bordered init">
						<div class="portlet-body">
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold " id="trgt_templateName_Id">
										Target Template creation - <span style="color: #337ab7;">
										<a onClick="validateHrefVulnerability(this)"  href="listdataview?idData=${trgtTemplateId}&dataLocation=FileSystem&name=${trgtTemplateName}&description=${trgtTemplateDescription}">
											${trgtTemplateName}</a></span>
									</span> 
									
									<input type="hidden" id="trgt_template_id" name="trgtTemplateId" value="${trgtTemplateId}" /> 
									<input type="hidden" id="trgt_template_name" name="trgtTemplateName" value="${trgtTemplateName}" /> 
									<input type="hidden" id="trgt_template_uniqueId" name="trgtTemplateUniqueId" value="${trgtTemplateUniqueId}" /> 
									<input type="hidden" id="trgt_templ_timerid" value=1 /> 
								</div>
							</div>

							<div class="portlet-body">
								<div class="note note-info task-in-progress  hidden"
									id="qs_trgt_templ_task_in_progress">
									Task in progress <i class="fa fa-spinner fa-spin"></i>
								</div>

								<div class="note note-info task-completed hidden"
									id="qs_trgt_templ_task_completed">
									Task queued <i class="fa fa-check"></i>
								</div>

								<div class="progress hidden" id="qs_trgt_templ_percentagebar">
									<div class="progress">
										<div class="progress-bar" role="progressbar"
											style="width: 5%;" aria-valuenow="100" aria-valuemin="0"
											aria-valuemax="100" id="qs_trgt_templ_progressbar">5%</div>
									</div>
								</div>
							</div>
						</div>
					</div>					
					
					<!-- Validation Creation -->
					<div id="qs_appStage" class="portlet light bordered init hidden">
						<div class="portlet-body">
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">
										Validation Check  - <span style="color: #337ab7;" id="appLink_id"></span>											
									</span> 
									
									<input type="hidden" id="app_id" name="appId" /> 
									<input type="hidden" id="appName_Id" name="appName" value="${validationName}" /> 
									<input type="hidden" id="app_timerid" value=1 />
									<input type="hidden" id="validationRunType" name="validationRunType" />
								</div>
							</div>

							<div class="portlet-body">
								<div class="note note-info task-in-progress  hidden"
									id="qs_app_task_in_progress">
									Task in progress <i class="fa fa-spinner fa-spin"></i>
								</div>

								<div class="note note-info task-completed hidden"
									id="qs_app_task_completed">
									Task queued <i class="fa fa-check"></i>
								</div>

								<div class="progress hidden" id="qs_app_percentagebar">
									<div class="progress">
										<div class="progress-bar" role="progressbar"
											style="width: 5%;" aria-valuenow="100" aria-valuemin="0"
											aria-valuemax="100" id="qs_app_progressbar">5%</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					
					<div id="messageDivId" class="hidden">
						<span id="messageId" style="color:red;"></span>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


<jsp:include page="footer.jsp" />
<script>
$(document).ready(function() {
	 {
	      var srcFileDownloadStatus = $("#src_fileDownloadStatus_id").val();
	      var trgtFileDownloadStatus = $("#trgt_fileDownloadStatus_id").val();
	      
	      var srcTemplateCreationStatus = "N";
	      var trgtTemplateCreationStatus = "N";
	      var pstatus_srcTemplate = "N";
	      var pstatus_trgtTemplate = "N";
	      
	     if((srcFileDownloadStatus=='success') && (trgtFileDownloadStatus=='success')){
	    	
	    	// Checking progress of source template 
	    	$('#src_fileDwnldSuccessDiv_id').removeClass('hidden').show();
		    $('#qs_src_templateStage').removeClass('hidden').show();
	    	$('#qs_src_templ_task_completed').removeClass('hidden').show();
	       
	        var src_form_data = {
	        		idData: $("#src_template_id").val(),
	        		uniqueId: $("#src_template_uniqueId").val()
	        };
	        
            var srcInterval = setInterval(function(){
                if($("#src_templ_timerid").length > 0){
                    //callAjaxFunc to check the current status of task-in-progress
                    $.ajax({
                        url: 'templateStatusPoll',
                        type: 'POST',
                        headers: { 'token':$("#token").val()},
                        datatype: 'json',
                        data: src_form_data,
                        success: function(message1) {
                            var j_obj1 = $.parseJSON(message1);
                            
                            if (j_obj1.hasOwnProperty('success')) {
                            	$('#qs_src_templ_task_completed').html(j_obj1.success);
                            
	                            if (j_obj1.success == 'Task started'){
	       	                       $('#qs_src_templ_task_completed').removeClass('hidden').show();
	                            }
	                            
	                            if(j_obj1.success=="Task in progress"){
	                            	var percentage=j_obj1.percentage;
	                            	console.log(percentage);
	                            	$('#qs_src_templ_task_completed').addClass('hidden').hide();
	                            	$('#qs_src_templ_task_in_progress').removeClass('hidden').show();
	                            	 $('#qs_src_templ_percentagebar').removeClass('hidden').show();
	                            	 $("#qs_src_templ_progressbar").css("width", percentage+'%');
	                            	 $('#qs_src_templ_progressbar').html(percentage+'%');
	                            }
	                            
	                            console.log(j_obj1.success);
	                            if (j_obj1.success == 'Task completed' || j_obj1.success == 'Task failed'
	                            		||  j_obj1.success == 'Task killed'){
	                            	$('#qs_src_templ_percentagebar').addClass('hidden');
	                                clearInterval(srcInterval);
	        	                    $('#qs_src_templ_task_in_progress').addClass('hidden').hide();
	                                $('#qs_src_templ_task_completed').removeClass('hidden').show();
	                                
	                                if(j_obj1.success == 'Task completed'){
	                                	srcTemplateCreationStatus = "Y";
	                                } else {
	                                	$('#messageDivId').removeClass('hidden').show();
	                                	$('#messageId').html("Source template creation failed !! Unable to proceed to validation!!");
	                                }
	                                
	                      	      pstatus_srcTemplate = "Y";
	                            }

                            }
                            if (j_obj1.hasOwnProperty('fail')) {
                            	 $('#qs_src_templ_percentagebar').addClass('hidden');
                            console.log(j_obj1.fail);
                            }
                        },
                        error: function(xhr, textStatus, errorThrown) 
                        {
                            console.log(errorThrown);
                        }
                    });
                    return false;
                }
                else
                {
                    clearInterval(srcInterval);
                    $('#qs_src_templ_percentagebar').addClass('hidden');
                }
                }, 1500);
            
            
            // Checking target template status
            $('#trgt_fileDwnldSuccessDiv_id').removeClass('hidden').show();
		    $('#qs_trgt_templateStage').removeClass('hidden').show();
	    	$('#qs_trgt_templ_task_completed').removeClass('hidden').show();
	       
	       
	        var trgt_form_data = {
	        		idData: $("#trgt_template_id").val(),
	        		uniqueId: $("#trgt_template_uniqueId").val()
	        };
	        
            var trgtInterval = setInterval(function(){
                if($("#trgt_templ_timerid").length > 0){
                    //callAjaxFunc to check the current status of task-in-progress
                    $.ajax({
                        url: 'templateStatusPoll',
                        type: 'POST',
                        headers: { 'token':$("#token").val()},
                        datatype: 'json',
                        data: trgt_form_data,
                        success: function(message1) {
                            var j_obj1 = $.parseJSON(message1);
                            
                            if (j_obj1.hasOwnProperty('success')) {
                            	$('#qs_trgt_templ_task_completed').html(j_obj1.success);
                            
	                            if (j_obj1.success == 'Task started'){
	       	                       $('#qs_trgt_templ_task_completed').removeClass('hidden').show();
	                            }
	                            
	                            if(j_obj1.success=="Task in progress"){
	                            	var percentage=j_obj1.percentage;
	                            	console.log(percentage);
	                            	$('#qs_trgt_templ_task_completed').addClass('hidden').hide();
	                            	$('#qs_trgt_templ_task_in_progress').removeClass('hidden').show();
	                            	 $('#qs_trgt_templ_percentagebar').removeClass('hidden').show();
	                            	 $("#qs_trgt_templ_progressbar").css("width", percentage+'%');
	                            	 $('#qs_trgt_templ_progressbar').html(percentage+'%');
	                            }
	                            
	                            console.log(j_obj1.success);
	                            if (j_obj1.success == 'Task completed' || j_obj1.success == 'Task failed'
	                            		||  j_obj1.success == 'Task killed'){
	                            	$('#qs_trgt_templ_percentagebar').addClass('hidden');
	                                clearInterval(trgtInterval);
	        	                    $('#qs_trgt_templ_task_in_progress').addClass('hidden').hide();
	                                $('#qs_trgt_templ_task_completed').removeClass('hidden').show();
	                                
	                                if(j_obj1.success == 'Task completed'){
	                                	trgtTemplateCreationStatus = "Y";
	                                } else {
	                                	$('#messageDivId').removeClass('hidden').show();
	                                	$('#messageId').html("Target template creation failed !! Unable to proceed to validation!!");
	                                }
	                    
	                      	      pstatus_trgtTemplate = "Y"; 
	                            }

                            }
                            if (j_obj1.hasOwnProperty('fail')) {
                            	 $('#qs_trgt_templ_percentagebar').addClass('hidden');
                            	console.log(j_obj1.fail);
                            }
                        },
                        error: function(xhr, textStatus, errorThrown) 
                        {
                            console.log(errorThrown);
                        }
                    });
                    return false;
                }
                else
                {
                    clearInterval(trgtInterval);
                    $('#qs_trgt_templ_percentagebar').addClass('hidden');
                }
                }, 1500);
	   		  } 
	          else
	   		  {
   					$('#qs_src_templateStage').addClass('hidden');
   					$('#qs_trgt_templateStage').addClass('hidden');
   					
   					var msg = "";
   					
   				 	if ((trgtFileDownloadStatus!='success') && (srcFileDownloadStatus!='success')){
						$('#src_fileDwnldSuccessDiv_id').addClass('hidden');
		  		    	$('#src_fileDwnldFailDiv_id').removeClass('hidden').show();
						$('#trgt_fileDwnldSuccessDiv_id').addClass('hidden');
 	  		    		$('#trgt_fileDwnldFailDiv_id').removeClass('hidden').show();
						msg = "Source and Target files download failed !! Unable to proceed to template creation!!";
					} else {

						if(trgtFileDownloadStatus=='success'){
		   	  		       $('#trgt_fileDwnldFailDiv_id').addClass('hidden');
	   					   $('#trgt_fileDwnldSuccessDiv_id').removeClass('hidden').show();
	   					} else {
	   					   $('#trgt_fileDwnldSuccessDiv_id').addClass('hidden');
	   	  		    	   $('#trgt_fileDwnldFailDiv_id').removeClass('hidden').show();
	   					   msg = "Target file download failed !! Unable to proceed to template creation!!";
	   					}
	   					
	   					if(srcFileDownloadStatus=='success'){
	   						$('#src_fileDwnldFailDiv_id').addClass('hidden');
	   		  		    	$('#src_fileDwnldSuccessDiv_id').removeClass('hidden').show();
	   					} else {
	   						$('#src_fileDwnldSuccessDiv_id').addClass('hidden');
	   		  		    	$('#src_fileDwnldFailDiv_id').removeClass('hidden').show();
	   	            		msg = "Source file download failed !! Unable to proceed to template creation!!";
	   					}
   					}
   					
   					$('#messageDivId').removeClass('hidden').show();
   					$('#messageId').html(msg);
	   		  }
	     	  
	          var validationInterval = setInterval(function(){
	        	  
		          if(pstatus_srcTemplate=="Y" && pstatus_trgtTemplate=="Y"){
		        	  console.log("templates ready")
		        	  clearInterval(validationInterval);
		        	  createDataMatchingValidation();
		          } else {
		        	  console.log("templates not ready")
		          }
		          
		        },1500);
		     
		        return false;
	 		}
	    });
</script>
<script>

function createDataMatchingValidation(){
	  var form_data = {
			srcTemplateId : $("#src_template_id").val(),
			srcTemplateName : $("#src_template_name").val(),
			trgtTemplateId : $("#trgt_template_id").val(),
			trgtTemplateName : $("#trgt_template_name").val(),
			dmValidationName : $("#appName_Id").val()
	  };
	  
	  $.ajax({
			url : 'createDataMatchingValidtion',
			type : 'POST',
			headers: { 'token':$("#token").val()},
			datatype : 'json',
			data : form_data,
			success : function(message) {
				var j_obj = $.parseJSON(message);
				var status = j_obj.status;
				
				if(status=='success'){
					var appName = j_obj.appName;
					var appId = j_obj.appId;
					var validationRunType = j_obj.validationRunType;
					console.log("appName:"+appName)
					console.log("appId:"+appId)
					$('#app_id').val(appId);
					$('#appName_Id').val(appName);
					$('#validationRunType').val(validationRunType);
					var srcTemplateId = $("#src_template_id").val();
					var srcTemplateName = $("#src_template_name").val();
					var appLink = "<a onClick='validateHrefVulnerability(this)'  href=\"customizeValidation?idApp="+appId+"&laName="+appName+"&idData="+srcTemplateId+"&lsName="+srcTemplateName+"\">"+appName+"</a>";
					$('#appLink_id').html(appLink);
					$('#qs_app_task_completed').removeClass('hidden').show();
					triggerApplication();	
				} else {
					$('#messageDivId').removeClass('hidden').show();
   					$('#messageId').html("Failed to creation validation!! Unable to proceed further!!");
				}
				
			}
	 });
}

function triggerApplication() {
	
	$('#qs_appStage').removeClass('hidden').show();

    var idApp =  $("#app_id").val();
    var srcTemplateName  = $("#src_template_name").val();
	var trgtTemplateName = $("#trgt_template_name").val();
	
    var form_data = {
			idApp1 : $("#app_id").val(),
			task_name : $("#appName_Id").val(),
			validationRunType : $("#validationRunType").val()
    };
    
	$.ajax({
		url : 'runTaskResult',
		type : 'POST',
		headers: { 'token':$("#token").val()},
		datatype : 'json',
		data : form_data,
		success : function(message) {
			// Get the unique Id to fetch status
			var j_obj = $.parseJSON(message);
			var uniqueId = j_obj.uniqueId;
			console.log("uniqueId:"+uniqueId)
			
			var form_data_1 = {
						idApp1 : $("#app_id").val(),
						uniqueId : uniqueId
			};
			
		    var myInterval = setInterval(function(){
		        if($("#app_timerid").length > 0){
		            //callAjaxFunc to check the current status of task-in-progress
		            $.ajax({
		                url: 'statusPoll',
		                type: 'POST',
		                headers: { 'token':$("#token").val()},
		                datatype: 'json',
		                data: form_data_1,
		                success: function(message1) {
		                    var j_obj1 = $.parseJSON(message1);
		                    
		                    if (j_obj1.hasOwnProperty('success')) {
		                    	$('#qs_app_task_completed').html(j_obj1.success);
		                    
		                        if (j_obj1.success == 'Task started'){
		   	                       $('#qs_app_task_completed').removeClass('hidden').show();
		                        }
		                        
		                        if(j_obj1.success=="Task in progress"){
		                        	var percentage=j_obj1.percentage;
		                        	console.log(percentage);
		                        	$('#qs_app_task_completed').addClass('hidden').hide();
		                        	$('#qs_app_task_in_progress').removeClass('hidden').show();
		                        	 $('#qs_app_percentagebar').removeClass('hidden').show();
		                        	 $("#qs_app_progressbar").css("width", percentage+'%');
		                        	 $('#qs_app_progressbar').html(percentage+'%');
		                        }
		                        
		                        console.log(j_obj1.success);
		                        if (j_obj1.success == 'Task completed' || j_obj1.success == 'Task failed'){
		                        	$('#qs_app_percentagebar').addClass('hidden');
		                            clearInterval(myInterval);
		    	                    $('#qs_app_task_in_progress').addClass('hidden').hide();
		                            $('#qs_app_task_completed').removeClass('hidden').show();
		                            
		                            $('#messageDivId').removeClass('hidden').show();

		                            var resultLink = "getMatchTablesData?appId="+idApp+"&source1="+srcTemplateName+"&source2="+trgtTemplateName;
                                	$('#messageId').html("<a onClick='validateHrefVulnerability(this)'  href=\""+resultLink+"\" style=\"color:red;\">Click here for results !!</a>");
		                        }
		                        
		                        if (j_obj1.success == 'Task killed'){
		                        	$('#qs_app_percentagebar').addClass('hidden');
		                            clearInterval(myInterval);
		    	                    $('#qs_app_task_in_progress').addClass('hidden').hide();
		                            $('#qs_app_task_completed').removeClass('hidden').show();
		                            
		                            $('#messageDivId').removeClass('hidden').show();
	                            	$('#messageId').html("Validation job is killed !! Unable to proceed to results!!");
	                        	}
		
		                    }
		                    if (j_obj1.hasOwnProperty('fail')) {
		                    	 $('#qs_app_percentagebar').addClass('hidden');
		                    console.log(j_obj1.fail);
		                    }
		                },
		                error: function(xhr, textStatus, errorThrown) 
		                {
		                    console.log(errorThrown);
		                }
		            });
		            return false;
		        }
		        else
		        {
		            clearInterval(myInterval);
		            $('#qs_src_templ_percentagebar').addClass('hidden');
		        }
		        }, 1500);
			}
		});
}
</script>
