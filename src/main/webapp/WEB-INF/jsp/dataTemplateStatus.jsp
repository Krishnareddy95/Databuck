<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<!-- BEGIN CONTENT -->
<head>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap.min.css">
<script
	src="./assets/global/plugins/jquery.min.js"></script>
<script
	src="./assets/global/plugins/bootstrap.min.js"></script>
</head>
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init" style="overflow-y: auto;">

					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold " id="templateName_Id">
							${templateName}
							</span>
							
							 <input type="hidden" id=template_id name="templateId"
								value="${templateId}" /> 
							 <input type="hidden" id="unique_id" name="uniqueId"
								value="${uniqueId}" /> 
							 <input type="hidden" id="timerid" value=1 />

						</div>
					</div>

					<div class="portlet-body caption font-red-sunglo">
						<span class="caption-subject bold "> ${message}</span>
					</div>

					<div class="portlet-body">
						<div class="note note-info task-in-progress  hidden">
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
				</div>

				<!-- END SAMPLE FORM PORTLET-->
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />

<script>
$(document).ready(function() {
	    {
	    	$('.task-completed').removeClass('hidden').show();

	        var form_data = {
	        		idData: $("#template_id").val(),
	        		uniqueId : $("#unique_id").val()
	        };
	        
            var myInterval = setInterval(function(){
                if($("#timerid").length > 0){
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
                            	$('.task-completed').html(j_obj1.success);
                            
	                            if (j_obj1.success == 'Task started'){
	       	                       $('.task-completed').removeClass('hidden').show();
	                            }
	                            
	                            if(j_obj1.success=="Task in progress"){
	                            	var percentage=j_obj1.percentage;
	                            	console.log(percentage);
	                            	$('.task-completed').addClass('hidden').hide();
	                            	$('.task-in-progress').removeClass('hidden').show();
	                            	 $('#percentagebar').removeClass('hidden').show();
	                            	 $("#progressbar").css("width", percentage+'%');
	                            	 $('#progressbar').html(percentage+'%');
	                            }
	                            
	                            console.log(j_obj1.success);
	                            if (j_obj1.success == 'Task completed' || j_obj1.success == 'Task failed'
	                            	|| j_obj1.success == 'Task killed'){
	                            	 $('#percentagebar').addClass('hidden');
	                                clearInterval(myInterval);
	        	                    $('.task-in-progress').addClass('hidden').hide();
	                                $('.task-completed').removeClass('hidden').show();
	                            }

                            }
                            if (j_obj1.hasOwnProperty('fail')) {
                            	 $('#percentagebar').addClass('hidden');
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
                    $('#percentagebar').addClass('hidden');
                }
                }, 1500);
	        }
	        
	        return false;
	    });
</script>