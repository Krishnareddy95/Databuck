
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp"/>

<jsp:include page="container.jsp"/>


		
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
									<span class="caption-subject bold "> Change Password <br><h3>${updatePwdStatus }</h3></span>
								</div>
							</div>
							<div class="portlet-body">
							<%System.out.println(request.getAttribute("updatePwdStatus")); %>
								<form id="myForm" action="sendNewPassword" class="form-horizontal" method="POST" accept-charset="utf-8">
											<input type="hidden" id="tourl" value="sendNewPassword" />
									    	
										<div class="form-body">
												<div class="row">
													<div class="col-md-6 col-capture">
							                            <div class="form-group form-md-line-input">
							                              <br>  <input type="password" class="form-control catch-error" id="oldpasswordid" name="oldpassword">
							                                <label for="oldpasswordid">Current Password *</label>
							                            </div><br/>
							                            <span class="required"></span>
						                       		</div>
						                       	</div>
						                       		
						                       	<div class="row">
						                       		<div class="col-md-6 col-capture">
							                            <div class="form-group form-md-line-input">
							                               <br> <input type="password" class="form-control catch-error" id="newpasswordid" name="newpassword" placeholder="" >
							                                <label for="form_control_1">New Password *</label>
							                            </div><br/>
							                            <span class="required"></span>
						                       		</div>
						                       	</div>

						                       	<div class="row">

						                       		<div class="col-md-6 col-capture">
							                            <div class="form-group form-md-line-input">
							                               <br> <input type="password" class="form-control catch-error" id="newpasswordid1" name="newpassword" placeholder="" >
							                                <label for="newpasswordid1">Retype New Password *</label>
							                            </div><br/>
							                            <span class="required"></span>
						                       		</div>
						                       	</div>	
											</div>

											<div class="form-actions noborder align-center">
												<!-- <div class="col-md-offset-3 col-md-9"> -->
												<input type="button" id="usrpswdsubmitbtnid" class="btn blue" value="submit">
													<!-- <button type="submit" id="usrpswdsubmitbtnid" class="btn blue">Submit</button> -->
													<!-- <a href="javascript:history.go(-1)" class="btn blue" style="margin-left:100px;">Cancel</a> -->
												<!-- </div> -->
											</div>
										</form>										<!-- END FORM-->
									</div>
								</div>
							</div>	
								
							</div>
						</div>
		<!--       *************END EXAMPLE TABLE PORTLET*************************-->
	
					</div>
				</div>
			</div>
		</div>
	

	
	                  <!--********     BEGIN CONTENT ********************-->

<jsp:include page="footer.jsp"/>

<head>
<script> 
        // wait for the DOM to be loaded 
        $(document).ready(function() { 
        	$('#myForm').ajaxForm({
        		headers: {'token':$("#token").val()}
        	}); 
        }); 
 </script> 

</head>