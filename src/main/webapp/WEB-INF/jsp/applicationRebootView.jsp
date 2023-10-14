
<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />



<!--============= BEGIN CONTENT BODY============= -->


<!--*****************      BEGIN CONTENT **********************-->


<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-12">

				<!--****         BEGIN EXAMPLE TABLE PORTLET       **********-->

				<div class="portlet light bordered">
				 <div class="init-form portlet-body">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Databuck Application Restart </span><br />

							<font size="4" color="red"></font>
						</div>
					</div>
					<div class="form-actions noborder align-center">
					<button type="submit" id="submitRebootId" name="submitRebootId"
										class="btn blue Submit">Reboot</button>
					</div>
				</div>
				
				<br/><br/>
					<div id="rs_failed_message_div" class="note note-info hidden">
						Databuck Reboot failed ..
					</div>
					
					<div
						class="note note-info task-in-progress hidden">
						Start Databuck Rebooting...... <i class="fa fa-spinner fa-spin"></i>
					</div>

					<div class="progress hidden" id="percentagebar">
						<div class="progress">
							<div class="progress-bar" role="progressbar" style="width: 5%;"
								aria-valuenow="100" aria-valuemin="0" aria-valuemax="100"
								id="progressbar">5%</div>
						</div>
					</div>
					
				</div>
				<!--       *************END EXAMPLE TABLE PORTLET*************************-->

			</div>
		</div>
	</div>
</div>

<jsp:include page="footer.jsp" />


<script>
$('#submitRebootId')
.click(
		function() {
			var percentage  = 10 ; 
			$.ajax({
			    type : 'GET',
			    url :"./triggerRebootScript",
			    dataType: 'html',
			    timeout: 10000,
			    beforeSend: function(){
			        /* $('.my-box').html('<div class="progress"><div class="progress-bar progress-bar-success progress-bar-striped active" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div></div>');
			        $('.progress-bar').animate({width: "30%"}, 100); */
			        $('#submitRebootId').addClass('hidden').hide();
			    	$('.task-in-progress').removeClass('hidden').show();
			    
			    },
			    success: function(message){ 
			    	var data = $.parseJSON(message);
			    	console.log(data);
			        if(data.success == 'Databuck Server Re-started Successfully'){
			        	
			            /* location.href = 'login_process'; */
			        	window.setTimeout(function(){

			                // Move to a new location or you can do something else
			                window.location.href = 'login_process';

			            }, 60000);
			        }else{
			        	/* alert(data.success);
			            $('.progress-bar').animate({width: "100%"}, 100);
			            setTimeout(function(){
			                $('.progress-bar').css({width: "100%"});
			                setTimeout(function(){
			                    $('.my-box').html(data);
			                }, 100);
			            }, 500); */
			        	//$('.init-form').addClass('hidden').hide();
				    	$('#rs_failed_message_div').removeClass('hidden').show(); 
				    	$('.task-in-progress').addClass('hidden');
			        }
			    },
			    error: function(request, status, err) {
			        alert((status == "timeout") ? "Timeout" : "error: " + request + status + err);
			    }
			});
			
			
		});

</script>







<!--********     BEGIN CONTENT ********************-->
