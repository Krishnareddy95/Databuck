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
								<div class="caption font-red-sunglo hidden" id="fileDwnldSuccessDiv_id">
									<span class="caption-subject bold "> File download to
										DATABUCK_HOME is successful !! </span>

									<div class="portlet-body" style="color: #337ab7;">${file_path}</div>
								</div>
								
								<div class="caption font-red-sunglo hidden" id="fileDwnldFailDiv_id">
									<span class="caption-subject bold "> File download to
										DATABUCK_HOME is failed !! </span>

									<div class="portlet-body" style="color: #337ab7;">${fileDownloadMessage}</div>
								</div>
							</div>
						</div>
					</div>
					
					<!-- Template creation -->
					<div id="qs_templateStage" class="portlet light bordered init">
						<div class="portlet-body">
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold " id="templateName_Id">
										Template creation - <span style="color: #337ab7;">
										<a onClick="validateHrefVulnerability(this)"  href="listdataview?idData=${templateId}&dataLocation=FileSystem&name=${templateName}&description=${templateDescription}">
											${templateName}</a></span>
									</span> 
									
									<input type="hidden" id="template_id" name="templateId" value="${templateId}" /> 
									<input type="hidden" id="template_name" name="templateName" value="${templateName}" /> 
									<input type="hidden" id="template_uniqueId" name="templateUniqueId" value="${templateUniqueId}" /> 
									<input type="hidden" id="templ_timerid" value=1 />
									<input type="hidden" id="fileDownloadStatus_id" value="${fileDownloadStatus}" />
								</div>
							</div>

							<div class="portlet-body">
								<div class="note note-info task-in-progress  hidden"
									id="qs_templ_task_in_progress">
									Task in progress <i class="fa fa-spinner fa-spin"></i>
								</div>

								<div class="note note-info task-completed hidden"
									id="qs_templ_task_completed">
									Task queued <i class="fa fa-check"></i>
								</div>

								<div class="progress hidden" id="qs_templ_percentagebar">
									<div class="progress">
										<div class="progress-bar" role="progressbar"
											style="width: 5%;" aria-valuenow="100" aria-valuemin="0"
											aria-valuemax="100" id="qs_templ_progressbar">5%</div>
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
									<input type="hidden" id="appName_Id" name="appName" /> 
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
	      var fileDownloadStatus = $("#fileDownloadStatus_id").val();
	      
	     if(fileDownloadStatus=='success'){
	    	
	    	$('#fileDwnldSuccessDiv_id').removeClass('hidden').show();
		    $('#qs_templateStage').removeClass('hidden').show();
	    	$('#qs_templ_task_completed').removeClass('hidden').show();
	       
	        var form_data = {
	        		idData: $("#template_id").val(),
	        		uniqueId : $("#template_uniqueId").val()
	        };
	        
            var myInterval = setInterval(function(){
                if($("#templ_timerid").length > 0){
                    //callAjaxFunc to check the current status of task-in-progress
                    $.ajax({
                        url: 'templateStatusPoll',
                        type: 'POST',
                        headers: { 'token':$("#token").val()},
                        datatype: 'json',
                        data: form_data,
                        success: function(message1) {
                            var j_obj1 = $.parseJSON(message1);
                            
                            if (j_obj1.hasOwnProperty('success')) {
                            	$('#qs_templ_task_completed').html(j_obj1.success);
                            
	                            if (j_obj1.success == 'Task started'){
	       	                       $('#qs_templ_task_completed').removeClass('hidden').show();
	                            }
	                            
	                            if(j_obj1.success=="Task in progress"){
	                            	var percentage=j_obj1.percentage;
	                            	console.log(percentage);
	                            	$('#qs_templ_task_completed').addClass('hidden').hide();
	                            	$('#qs_templ_task_in_progress').removeClass('hidden').show();
	                            	 $('#qs_templ_percentagebar').removeClass('hidden').show();
	                            	 $("#qs_templ_progressbar").css("width", percentage+'%');
	                            	 $('#qs_templ_progressbar').html(percentage+'%');
	                            }
	                            
	                            console.log(j_obj1.success);
	                            if (j_obj1.success == 'Task completed' || j_obj1.success == 'Task failed'
	                            		||  j_obj1.success == 'Task killed'){
	                            	$('#qs_templ_percentagebar').addClass('hidden');
	                                clearInterval(myInterval);
	        	                    $('#qs_templ_task_in_progress').addClass('hidden').hide();
	                                $('#qs_templ_task_completed').removeClass('hidden').show();
	                                
	                                if(j_obj1.success == 'Task completed'){
	                                	getAppDetailsForTemplate();
	                                } else {
	                                	$('#messageDivId').removeClass('hidden').show();
	                                	$('#messageId').html("Template creation failed !! Unable to proceed to validation!!");
	                                }
	                            }

                            }
                            if (j_obj1.hasOwnProperty('fail')) {
                            	 $('#qs_templ_percentagebar').addClass('hidden');
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
                    $('#qs_templ_percentagebar').addClass('hidden');
                }
                }, 1500);
	   		  } else
	   			 {
	  	    		$('#fileDwnldSuccessDiv_id').addClass('hidden');
	  		    	$('#fileDwnldFailDiv_id').removeClass('hidden').show();
	  		   		$('#qs_templateStage').addClass('hidden');
	   				$('#messageDivId').removeClass('hidden').show();
            		$('#messageId').html("File download failed !! Unable to proceed to template creation!!");
	   			 }
	        }
	        
	        return false;
	    });
</script>
<script>

function getAppDetailsForTemplate(){
	  var form_data = {
			idData : $("#template_id").val(),
			templateName : $("#template_name").val()
	  };
	  
	  $.ajax({
			url : 'getAppDetailsByTemplate',
			type : 'POST',
			headers: { 'token':$("#token").val()},
			datatype : 'json',
			data : form_data,
			success : function(message) {
				var j_obj = $.parseJSON(message);
				var appName = j_obj.appName;
				var appId = j_obj.appId;
				var validationRunType = j_obj.validationRunType;
				console.log("appName:"+appName)
				console.log("appId:"+appId)
				$('#appName_Id').val(appName);
				$('#app_id').val(appId);
				$('#validationRunType').val(validationRunType);
				var templateId = $("#template_id").val();
				var templateName = $("#template_name").val();
				var appLink = "<a onClick='validateHrefVulnerability(this)'  href=\"customizeValidation?idApp="+appId+"&laName="+appName+"&idData="+templateId+"&lsName="+templateName+"\">"+appName+"</a>";
				$('#appLink_id').html(appLink);
				$('#qs_app_task_completed').removeClass('hidden').show();
				triggerApplication();
			}
	 });
}

function triggerApplication() {
	
	$('#qs_appStage').removeClass('hidden').show();

    var idApp =  $("#app_id").val();
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
		                            var resultPageUrl = 'dashboard_table?idApp=';
		                            var resultLink=resultPageUrl+idApp;
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
		            $('#qs_templ_percentagebar').addClass('hidden');
		        }
		        }, 1500);
			}
		});
}
</script>
