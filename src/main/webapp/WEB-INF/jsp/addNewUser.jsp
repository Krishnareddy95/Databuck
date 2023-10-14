<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<script>
	var request;
	var vals="";
	function sendInfo() {
		var v = document.getElementById("userName").value;
		//var v=document.vinform.Database.value;  
		var url = "./duplicateEmail?val=" + v;
		//alert("in ajax code");
		if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}

		try {
			request.onreadystatechange = getInfo;
			request.open("POST", url, true);
			request.setRequestHeader('token',$("#token").val());
			request.send();
		} catch (e) {
			alert("Unable to connect to server");
		}
	}

	function getInfo() {
		if (request.readyState == 4) {
			var val = request.responseText;
			//alert("getInfo");
			//alert(val);
			//var sal="Database already exists";
			//console.log(val);
			document.getElementById('amit').innerHTML = val;
		 	if (val!="") {
				vals=val;
				//alert("please enter another name");
				return false;
			}
			//alert("check123");
		}
	}
</script>
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
							<span class="caption-subject bold "> Create New User </span>
						</div>
					</div>
					<div class="portlet-body form">

						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="firstName" name="firstName"
											placeholder=" Enter First Name"> <label
											for="form_control_1">First Name </label>
									</div>
									<span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="lastName" name="lastName"
											placeholder=" Enter Last Name"> <label
											for="form_control_1">Last Name </label>
									</div>
								</div>
							</div>
						</div>

						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="userName" name="userName"
											placeholder=" Enter Email" onkeyup="sendInfo()"> <label
											for="form_control_1">Email</label>
									</div>
									<span id="amit" class="help-block required"></span>
									<!-- <br /> <span class="required"></span> -->
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="password" class="form-control" id="password"
											name="password" placeholder=" Enter Password "> <label
											for="form_control">Password</label>
									</div>
								</div>
							</div>

							<div class="form-body">
								<div class="row">
									<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control" id="roleid" name="roleid">
												<option value="">Select Role</option>
												<c:forEach var="roles" items="${Roles}">

													<option value="${roles.key}">${roles.value}</option>

												</c:forEach>
											</select> <label for="form_control_1" id="host-id">Select Role*</label>
										</div>
										<br /> <span class="required"></span>
									</div>
									<!-- <div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<input type="password" class="form-control" id="Pwd"
												name="Password" placeholder=" Enter Password "> <label
												for="form_control">Password</label> <span class="help-block">Short
												text about Data Set</span>
										</div>
									</div> -->
								</div>
							</div>
							<!-- <div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="Port" name="Port"
											placeholder=" Enter Port "> <label for="form_control">Port</label>
										<span class="help-block">Short text about Data Set</span>
									</div>
								</div>
								<div class="col-md-6 col-capture hidden" id="OracleXE">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="servicename"
											name="servicename" placeholder=" Enter Service Name">
										<label for="form_control">Service Name</label>
									</div>
								</div>
								<div class="col-md-6 col-capture hidden" id="MSSQLAD">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control" id="Domain"
											name="Domain" placeholder=" Enter Domain "> <label
											for="form_control">Domain</label>
									</div>
								</div>
							</div> -->
						</div>
					</div>
					<div class="form-actions noborder align-center"></div>
					<p align="center">
						<button type="submit" id="addNewUserid" class="btn blue">Submit</button>
					</p>
					<!-- </form> -->
					<!-- </form> -->
				</div>
			</div>
			<!-- END SAMPLE FORM PORTLET-->
		</div>
	</div>
</div>
<!-- END QUICK SIDEBAR -->
<jsp:include page="footer.jsp" />
<head>
<script>
$('#addNewUserid').click(function() {
    var no_error = 1;
    $('.input_error').remove();
    
	    if(!checkInputText()){ 
			no_error = 0;
			alert('Vulnerability in submitted data, not allowed to submit!');
		}
	    
    	if($("#firstName").val().length == 0) {
    		console.log('firstName');
    		  $('<span class="input_error" style="font-size:12px;color:red">Please enter First Name</span>').insertAfter($('#firstName'));
    	        no_error = 0;
    	}
    	if($("#lastName").val().length == 0) {
    		console.log('lastName');
  		  $('<span class="input_error" style="font-size:12px;color:red">Please enter Last Name</span>').insertAfter($('#lastName'));
  	        no_error = 0;
  	}
    	if($("#userName").val().length == 0) {
    		console.log('userName');
  		  $('<span class="input_error" style="font-size:12px;color:red">Please enter Email</span>').insertAfter($('#userName'));
  	        no_error = 0;
  	}   
    	if($("#password").val().length == 0) {
    		console.log('password');
  		  $('<span class="input_error" style="font-size:12px;color:red">Please enter Password</span>').insertAfter($('#password'));
  	        no_error = 0;
  	}   
    	if($("#roleid").val().length == 0) {
    		console.log('roleid');
  		  $('<span class="input_error" style="font-size:12px;color:red">Please Select a Role</span>').insertAfter($('#roleid'));
  	        no_error = 0;
  	}   
    	var amit1 = $('#amit').html();
         console.log(amit1);
         	if(amit1=="")
         	{
         		
         	}
         	else{
         		console.log("inside amit if");
                 no_error = 0;
         	} 
        if( no_error==0){
        	console.log('error');
            return false;
        }
    	 if (no_error) {    
             var form_data = {
            		 firstName: $("#firstName").val(),
            		 lastName: $("#lastName").val(),
            		 userName: $("#userName").val(),
            		 password: $("#password").val(),
            		 roleid: $("#roleid").val(),
             };

              console.log(form_data); 

             $.ajax({
                 url: "addNewUserIntoDatabase",
                 type: 'POST',
                 headers: { 'token':$("#token").val()},
                 datatype: 'json',
                 data: form_data,
                 success: function(message) {
                     var j_obj = $.parseJSON(message);
                     if (j_obj.hasOwnProperty('success')) {
                    	 toastr.info('New User Created Successfully');
                    	      setTimeout(function(){
                    	    	  window.location.href= "viewUsers";
                          },1000); 
                     } else {
                         if (j_obj.hasOwnProperty('fail')) {
                        	 toastr.info('There was a problem.');
                         }
                     }
                 },
                 error: function(xhr, textStatus, errorThrown) 
                 {
                	 toastr.info('There was a problem.');
                 }
             });
         }
         return false;
    	
    	
});


</script>
</head>