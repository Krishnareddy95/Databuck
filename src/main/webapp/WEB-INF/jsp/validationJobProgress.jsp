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
</head>
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">

					<div class="portlet-title">
						<div class="caption font-red-sunglo">
						    <input type="hidden" id="timerid" value=1 />
						    <input type="hidden" id="idApp" name="idApp" value="${idApp}">
						    <input type="hidden" id="uniqueId" name="uniqueId" value="${uniqueId}">
						    <input type="hidden" id="validationName" name="validationName" value="${validationName}">
						    <input type="hidden" id="jobExecStatus" name="jobExecStatus" value="${jobExecStatus}">
						    <input type="hidden" id="jobExecStatusMsg" name="jobExecStatusMsg" value="${jobExecStatusMsg}">
							<span class="caption-subject bold " id="appNameId">Validation Check : ${validationName}</span>
						</div>
					</div>

					<div class="note note-info task-failed-info hidden">
					</div>
					
					<div class="note note-info task-in-progress hidden">
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
<script src="./assets/global/plugins/jquery-ui.min.js"></script>

<script>

$(document).ready(function(){
	var idApp=$('#idApp').val();
	var validationName=$('#validationName').val();
	var uniqueId= $('#uniqueId').val();
	var jobExecStatus= $('#jobExecStatus').val();
	var jobExecStatusMsg= $('#jobExecStatusMsg').val();
	
	if(jobExecStatus === 'Y'){
	    showValidationProgress(idApp,validationName,uniqueId)
	} else {
		
		$('.task-failed-info').html(jobExecStatusMsg+"<span style='color:blue;'> Redirecting to Validation view page in few seconds.</span>");
		$('.task-failed-info').removeClass('hidden').show();
		
		window.setTimeout(function(){
			window.location.href = "validationCheck_View";
		}, 5000);
		
	}
});

	function showValidationProgress(idApp,validationName,uniqueId){

		var form_data1 = {
				idApp1 :idApp ,
				task_name :validationName ,
				uniqueId :uniqueId
		};

		$('.task-completed').removeClass('hidden').show();
		$('.task-in-progress').addClass('hidden').hide();
		$('.task-failed-info').addClass('hidden').hide();
		
		var myInterval = setInterval(
				function() {
					if ($("#timerid").length > 0) {
						$.ajax({
                            url : 'statusPoll',
                            type : 'POST',
                            headers: { 'token':$("#token").val()},
                            datatype : 'json',
                            data : form_data1,
                            success : function(message1) {
                                var j_obj1 = $.parseJSON(message1);
                                console.log(j_obj1);
                                if (j_obj1.hasOwnProperty('success')) {
                                    $('.task-completed').html(j_obj1.success);

                                    if (j_obj1.success == "Task in progress") {
                                        var percentage = j_obj1.percentage;

                                            console.log(percentage);
                                            $('#percentagebar').removeClass('hidden').show();
                                            $("#progressbar").css("width",percentage+ '%');
                                            $('#progressbar').html(percentage+ '%');
										}

											console.log(j_obj1.success);
											if (j_obj1.success == 'Task completed') {
												$('#percentagebar').addClass('hidden');
												clearInterval(myInterval);
											}
											if (j_obj1.success == 'Task failed' || j_obj1.success == 'Task killed') {
												$('#percentagebar').addClass('hidden');
												clearInterval(myInterval);
											}
										}
										if (j_obj1.hasOwnProperty('fail')) {
											$('#percentagebar').addClass('hidden');
											console.log(j_obj1.fail);
										}
									},
									error : function(
											xhr,
											textStatus,
											errorThrown) {

										console.log(errorThrown);
									}
								});
						return false;
					} else {
						clearInterval(myInterval);
						$('#percentagebar').addClass('hidden');
					}
				}, 1500);
		
	}
</script>