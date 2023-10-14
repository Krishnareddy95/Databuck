<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />

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
							<span class="caption-subject bold "> Create New Project </span>
						</div>
					</div>
					<div class="portlet-body form">
						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="projName" name="projName"
											placeholder=" Enter the Project Name"> <label
											for="form_control_1">Project Name *</label>										
									</div>
									<span class="required"></span>
								</div>
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="projDescription" name="projDescription"
											placeholder=" Enter the Project Description"> <label
											for="form_control_1">Project Description</label>
									</div>									
								</div>
							</div>
						</div>
						<div class="row">
							<span></span>
						</div>
						<div class="row">
							<span></span>
						</div>
						<div class="row">
													
							<div class="col-xs-2">
								<label>Assign Users</label>
							</div>
							<div class="col-xs-2">
								<label></label>
							</div>
							<div class="col-xs-2">
								<label></label>
							</div>															
							<!-- <div class="col-xs-2">
								<label>Assign Consumers</label>
							</div> -->
						</div>
						<div class="row">
							<div class="col-xs-2">
								<select name="from[]" id="grpMultiselect" class="form-control" size="8" multiple="multiple">
									<c:forEach items="${groupList}"
										var="groupListObj">
										<option value="${groupListObj.email}">${groupListObj.email}</option>
									</c:forEach>
								</select>
							</div>							
							<div class="col-xs-1">
								<button type="button" id="grpMultiselect_rightAll" class="btn btn-block"><i class="glyphicon glyphicon-forward"></i></button>
								<button type="button" id="grpMultiselect_rightSelected" class="btn btn-block"><i class="glyphicon glyphicon-chevron-right"></i></button>
								<button type="button" id="grpMultiselect_leftSelected" class="btn btn-block"><i class="glyphicon glyphicon-chevron-left"></i></button>
								<button type="button" id="grpMultiselect_leftAll" class="btn btn-block"><i class="glyphicon glyphicon-backward"></i></button>
							</div>						
							<div class="col-xs-2">
								<select name="to[]" id="grpMultiselect_to" class="form-control" size="8" multiple="multiple"></select>
							</div>
							<div class="col-xs-1">
								<label></label>
							</div>	
							<%-- <div class="col-xs-2">
								<select name="from[]" id="grpMultiselect2" class="form-control" size="8" multiple="multiple">
									<c:forEach items="${groupList}"
										var="groupListObj">
										<option value="${groupListObj.idUser}">${groupListObj.firstName}</option>
									</c:forEach>
								</select>
							</div> --%>
							
							<!-- <div class="col-xs-1">
								<button type="button" id="grpMultiselect2_rightAll" class="btn btn-block"><i class="glyphicon glyphicon-forward"></i></button>
								<button type="button" id="grpMultiselect2_rightSelected" class="btn btn-block"><i class="glyphicon glyphicon-chevron-right"></i></button>
								<button type="button" id="grpMultiselect2_leftSelected" class="btn btn-block"><i class="glyphicon glyphicon-chevron-left"></i></button>
								<button type="button" id="grpMultiselect2_leftAll" class="btn btn-block"><i class="glyphicon glyphicon-backward"></i></button>
							</div> -->						
							<!-- <div class="col-xs-2">
								<select name="to[]" id="grpMultiselect2_to" class="form-control" size="8" multiple="multiple"></select>
							</div> -->
						</div>												
					<div class="form-actions noborder align-center"></div>
					<p align="center">
						<button type="submit" id="btnSubmit" class="btn blue">Submit</button>
					</p>
					<!-- </form> -->
					<!-- </form> -->
				</div>
			</div>
			<!-- END SAMPLE FORM PORTLET-->
		</div>
	</div>
</div>
</div>
<!-- END QUICK SIDEBAR -->
<jsp:include page="footer.jsp" />
<head>
<script type="text/javascript">
	jQuery(document).ready(function($) {
		$('#grpMultiselect').multiselect();
		$('#grpMultiselect2').multiselect();
		/* $(groupList).each(function(i, item){
			 $('#grpMultiselect').append($('<option>', { 
			        value: item.idGroup,
			        text : item.groupName,
			    }));
		});	 */
	});
</script>
<script>	
	
	$('#btnSubmit').click(function() {
		 
	    var no_error = 1;
	   	    
		    if(!checkInputText()){ 
				no_error = 0;
				alert('Vulnerability in submitted data, not allowed to submit!');
			}
		    $('.input_error').remove();
	    
	    	if($("#projName").val().length == 0) {    		
	  		  $('<span class="input_error" style="font-size:12px;color:red">Project name is mandatory</span>').insertAfter($('#projName'));
	  	        no_error = 0;
	  		}  
	    	
    	 	var groupIds = "";
	   	    var count = 0;
	   	    $('#grpMultiselect_to').each(function () {
	   	        if (count == 0) {
	   	        	groupIds = $(this).val();
	   	        }
	   	        else {
	   	        	groupIds += "," + $(this).val();
	   	        }
	   	        count = count + 1;
	   	    });
	   	    
	   	 	/* var consumerGroupIds = "";
	   	    var count = 0;
	   	    $('#grpMultiselect2_to').each(function () {
	   	        if (count == 0) {
	   	        	consumerGroupIds = $(this).val();
	   	        }
	   	        else {
	   	        	consumerGroupIds += "," + $(this).val();
	   	        }
	   	        count = count + 1;
	   	    }); */
    	
	   	    
    	 if (no_error) {    
             var form_data = {            		
            		 projectName: $("#projName").val(),
            		 projectDescription: $("#projDescription").val(),
            		 selectedOwnerGroups: JSON.stringify(groupIds),
            		 //selectedConsumerGroups: JSON.stringify(consumerGroupIds),
             };

              console.log(form_data); 

             $.ajax({
                 url: "addNewProjectIntoDatabase",
                 type: 'POST',
                 headers: { 'token':$("#token").val()},
                 datatype: 'json',
                 data: form_data,
                 success: function(message) {
                     var j_obj = $.parseJSON(message);
                    
                     if (j_obj.hasOwnProperty('success')) {
                    	 toastr.info('New Project Inserted Successfully');
                    	      setTimeout(function(){
                    	    	  window.location.href= "viewProject";
                          },1000); 
                     } else {
                         if (j_obj.hasOwnProperty('duplicate')) {
                        	 toastr.info('Project Name is already Exist..');
                         }
                         else if(j_obj.hasOwnProperty('fail')) {
                        	 toastr.info('There was a problem in inserting group.');
                         }
                     }
                     
                 },
                 error: function(xhr, textStatus, errorThrown) 
                 {
                	 toastr.info('There was a problem in inserting group.');
                 }
             });
         }
         return false;
	});	
    	



</script>
					
</head>