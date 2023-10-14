<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<%@page isELIgnored="false" %>

<!-- BEGIN CONTENT -->
<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- END PAGE HEADER-->
		<div class="row main" id="main">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Delete Project :
								</span> 
								
						</div>
					</div>
				
				<div class="portlet-body form">

					<form role="form" method="get" action="delete_Template">
					
						<input type="hidden" id="id" value="${selectedProject.idProject }" /> 

						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error" id="name"
											value="${selectedProject.projectName }" readonly> <label
											for="form_control">Project Name</label>
									</div>
									<span class="required"></span>
								</div>
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="description" value="${selectedProject.projectDescription}"
											readonly> <label for="form_control">Description</label>
									</div>
									<span class="required"></span>
								</div>
							</div>




							<div class="form-actions noborder align-center">
								
								
                        <input type="button" value="Delete" class="btn red" id="btnSubmit" />
						<a onClick="validateHrefVulnerability(this)"  href="viewProject" class="btn default">Cancel</a>								
								
							</div>
							
							<input type="hidden" id="errorval" value="${Projectmessage}" />
							
						
  
</div>
 
							
	
                          
						</div>
					</form>
				</div>


			</div>
		</div>
		
				<div class="row hidden" id="errorMessage">
			<div class="col-md-12" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">
						  <div class="caption font-red-sunglo">
							<span class="caption-subject bold hidden" id="x1"> Project Deleted successfully</span>
							<span class="caption-subject bold hidden" id="x0"> Groups are associated with the project. Hence, project can't be deleted.</span>
							<span class="caption-subject bold hidden" id="x2"> There is some problem in deleting Project</span>
							<span class="caption-subject bold hidden" id="error"> There is some problem in deleting Project</span>
							
						</div>
					</div>
					</div>
					</div>
					</div>
		
		
		
		<div class="note note-info hidden">Data Template deleted
			Successfully</div>
		<div class="note note-danger hidden"></div>
		<!-- END SAMPLE FORM PORTLET-->
	</div>
</div>
</div>
<jsp:include page="footer.jsp" />
<head>

<script>	
	
	$('#btnSubmit').click(function() {
		 
	    var no_error = 1;   
	    	
	    	
    	 

	   	    
    	 if (no_error) {    
             var form_data = {            		
            		 projectId: $("#id").val(),
            		
             };

              console.log(form_data); 
          
             $.ajax({
                 url: "deleteProject",
                 type: 'POST',
                 headers: { 'token':$("#token").val()},
                 datatype: 'json',
                 data: form_data,
                 success: function(message) {
                     var j_obj = $.parseJSON(message);
                    
                     if (j_obj.hasOwnProperty('success')) {
                    	 $("#main").addClass("hidden");
                    	 $("#errorMessage").removeClass("hidden");
                    	 $("#x1").removeClass("hidden");
                     } else {
                         if (j_obj.hasOwnProperty('fail')) {
                        	 $("#main").addClass("hidden");
                        	 $("#errorMessage").removeClass("hidden");
                        	 $("#x0").removeClass("hidden");
                         }
                         else if(j_obj.hasOwnProperty('error')) {
                        	 $("#main").addClass("hidden");
                        	 $("#errorMessage").removeClass("hidden");
                        	 $("#x2").removeClass("hidden");
                         }
                     }
                     
                 },
                 error: function(xhr, textStatus, errorThrown) 
                 {
                	 $("#main").addClass("hidden");
                	 $("#errorMessage").removeClass("hidden");
                	 $("#error").removeClass("hidden");
                 }
             });
         }
         return false;
	});	
    	



</script>



</head>