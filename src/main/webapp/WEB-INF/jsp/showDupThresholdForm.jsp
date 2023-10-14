
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
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold ">Customize Values </span>
						</div>
					</div>
					<div class="portlet-body">
						<table class="table table-striped table-bordered table-hover"
							id="showDataTable">
							<thead>
							</thead>
							<div class="portlet-body form">
								<input type="hidden" id="tourl" value="createForm" />
								<!--  <form role="form" method="post" action="">  -->
								<%
								String idApp=request.getParameter("idApp");
								System.out.println("idapp value:"+idApp);
								
								out.println("<form action='saveAllAndIdentityThreashold?idApp="+idApp+"'"); %>
								enctype="multipart/form-data"
									method="POST" accept-charset="utf-8">

									<div class="form-body">
										<div class="row">
											<div class="col-md-6 col-capture">
												<div class="form-group form-md-line-input">
												<%-- <input type="hidden" name="idApp" value=<% request.getParameter("idApp"); %>/> --%>
													<input type="text" class="form-control catch-error"
														id="thresholdfield" name="threshold" placeholder=" Enter DupRow threshold for all fields">
													<label for="form_control_1">DupRow threshold for all fields</label>
													<!--  <span class="help-block">Data Set Name </span> -->
												</div>
												<br /> <span class="required"></span>
											</div>

											<div class="col-md-6 col-capture">
												<div class="form-group form-md-line-input">
													<input type="text" class="form-control" id="identityfield"
														name="identity" placeholder=" Enter DupRow threshold for identity fields"
														onkeyup="sendInfo()"> <label for="form_control">DupRow threshold for identity fields</label>
													<!-- <span class="help-block">Short text about Data Set</span> -->
												</div>
											</div>
										</div>
</div>
							</div>
									<p align="center">
											<button type="submit" id="customizeThreshold" class="btn blue">Submit</button>
										</p>
										<!-- </form> -->
								</form>
										
							

						</table>
					</div>
				</div>
				<div class="form-actions noborder align-center"></div>
														<!--       *************END EXAMPLE TABLE PORTLET*************************-->

			</div>
		</div>
	</div>
</div>
<!--********     BEGIN CONTENT ********************-->
<jsp:include page="footer.jsp" />
<script>
 $('#customizeThreshold').click(function(){
        var no_error = 1;
        $('.input_error').remove();
        
        if($("#thresholdfield").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter thresholdfield</span>').insertAfter($('#thresholdfield'));
            no_error = 0;
        }
        if($("#identityfield").val().length == 0){
            $('<span class="input_error" style="font-size:12px;color:red">Please enter identityfield</span>').insertAfter($('#identityfield'));
            no_error = 0;
        }
        
        if(! no_error){
            return false;
        }
      /*   if(no_error){
            var changepassword = {
                oldpassword : $("#oldpasswordid").val(),
                newpassword : $("#newpasswordid").val(),
                
                //times: new Date().getTime()
            };
          
             console.log(changepassword);
             //return false;
            
            $.ajax({
              url: './sendnewpassword',
              type: 'POST',
              headers: { 'token':$("#token").val()},
              datatype:'json',
              data: JSON.stringify(changepassword),
              contentType:"application/json",
              success: function(data) {
              	//alert(data);
                  if (data!="") {
                	 toastr.info(data);
                     // toastr.info('Item updated successfully');
                      setTimeout(function(){
                          window.location.reload();
                      },1000); 
                  } else {
                      toastr.info('There was a problem.');
                  } 
              },
              error: function(xhr, textStatus, errorThrown){
            	 toastr.info("error while updating");
                    $('#initial').hide();
                    $('#fail').show();
              }
           });
        }
        return false; */
    });
    </script>